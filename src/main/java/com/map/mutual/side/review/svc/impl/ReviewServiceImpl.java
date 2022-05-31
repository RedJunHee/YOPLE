package com.map.mutual.side.review.svc.impl;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.exception.YOPLETransactionException;
import com.map.mutual.side.common.fcmmsg.constant.FCMConstant;
import com.map.mutual.side.common.fcmmsg.svc.FCMService;
import com.map.mutual.side.common.utils.CryptUtils;
import com.map.mutual.side.review.model.dto.*;
import com.map.mutual.side.review.model.entity.*;
import com.map.mutual.side.review.model.enumeration.EmojiType;
import com.map.mutual.side.review.repository.*;
import com.map.mutual.side.review.svc.ReviewService;
import com.map.mutual.side.world.model.entity.WorldEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * fileName       : ReviewServiceImpl
 * author         : kimjaejung
 * createDate     : 2022/03/22
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/22        kimjaejung       최초 생성
 */
@Service
@Log4j2
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;
    @Autowired
    private FCMService fcmService;
    @Autowired
    private ReviewWorldMappingRepository reviewWorldPlaceMappingRepository;
    @Autowired
    private PlaceRepo placeRepo;
    @Autowired
    private EmojiStatusRepo emojiStatusRepo;
    @Autowired
    private EmojiRepo emojiRepo;
    @Autowired
    private EmojiStatusNotiRepo emojiStatusNotiRepo;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public ReviewDto createReview(ReviewPlaceDto dto) throws YOPLEServiceException {
        if (dto.getPlace() != null && !placeRepo.findById(dto.getPlace().getPlaceId()).isPresent()) {
            PlaceEntity placeEntity = PlaceEntity.builder()
                    .placeId(dto.getPlace().getPlaceId())
                    .name(dto.getPlace().getName())
                    .address(dto.getPlace().getAddress())
                    .roadAddress(dto.getPlace().getRoadAddress())
                    .categoryGroupCode(dto.getPlace().getCategoryGroupCode())
                    .categoryGroupName(dto.getPlace().getCategoryGroupName())
                    .x(dto.getPlace().getX())
                    .y(dto.getPlace().getY())
                    .build();

            placeRepo.save(placeEntity);
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        if (reviewRepo.existsByUserEntityAndPlaceEntity(UserEntity.builder().suid(userInfoDto.getSuid()).build(),
                PlaceEntity.builder().placeId(dto.getPlace().getPlaceId()).build())) {
            throw new YOPLETransactionException(ApiStatusCode.THIS_PLACE_IN_REVIEW_IS_ALREADY_EXIST);
        }


        ReviewDto result;
        ReviewEntity reviewEntity;
        if (dto.getReview().getImageUrls() == null || dto.getReview().getImageUrls().length == 0) {
            reviewEntity = ReviewEntity.builder()
                    .userEntity(UserEntity.builder().suid(userInfoDto.getSuid()).build())
                    .content(dto.getReview().getContent())
                    .imageUrl("")
                    .placeEntity(PlaceEntity.builder().placeId(dto.getPlace().getPlaceId()).build())
                    .build();
        } else {
            reviewEntity = ReviewEntity.builder()
                    .userEntity(UserEntity.builder().suid(userInfoDto.getSuid()).build())
                    .content(dto.getReview().getContent())
                    .placeEntity(PlaceEntity.builder().placeId(dto.getPlace().getPlaceId()).build())
                    .imageUrl(Arrays.stream(dto.getReview().getImageUrls()).map(String::toString).collect(Collectors.joining(",")))
                    .build();
        }
        result = saveReviewAndMappings(dto.getReview(), reviewEntity);
        return result;
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public ReviewDto updateReview(ReviewDto reviewDto) throws YOPLEServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
        ReviewDto result;
        try {
            ReviewEntity entity = reviewRepo.findByReviewId(reviewDto.getReviewId());
            if (entity == null) {
                throw new YOPLETransactionException(ApiStatusCode.CONTENT_NOT_FOUND);
            } else if (!entity.getUserEntity().getSuid().equals(userInfoDto.getSuid())) {
                throw new YOPLETransactionException(ApiStatusCode.FORBIDDEN);
            } else {

                if (reviewDto.getImageUrls() != null) {
                    entity.setImageUrl(Arrays.stream(reviewDto.getImageUrls()).map(String::toString).collect(Collectors.joining(",")));
                } else entity.setImageUrl(null);
                entity.setContent(reviewDto.getContent());
                result = saveReviewAndMappings(reviewDto, entity);
            }
        } catch (Exception e) {
            throw new YOPLETransactionException(ApiStatusCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Transactional(rollbackFor = {RuntimeException.class})
    public ReviewDto saveReviewAndMappings(ReviewDto reviewDto, ReviewEntity entity) throws YOPLEServiceException {
        ReviewEntity returnedReview;
        returnedReview = reviewRepo.save(entity);
        //공통 리뷰 저장

        List<Long> presentWorldIds = reviewWorldPlaceMappingRepository.findAllByReviewEntity(ReviewEntity.builder().reviewId(returnedReview.getReviewId()).build())
                .stream().map(data -> data.getWorldEntity().getWorldId()).collect(Collectors.toList());
        //현재 db에 매핑 돼있는 월드 id들 조회

        List<Long> receivedWorldIds = reviewDto.getWorldList();
        //생성, 수정 할 월드 id들 리스트
        presentWorldIds.removeAll(receivedWorldIds);
        //생성, 수정 할 월드 id가 아닐 시 제거 -> DB worldIds - 받은 worldIds = 삭제할 worldIds


        List<ReviewWorldMappingEntity> todoDeleteEntities = new ArrayList<>();
        //삭제하기 위한 리스트 엔티티 생성
        presentWorldIds.forEach(data -> {
            ReviewWorldMappingEntity mappingEntity = ReviewWorldMappingEntity.builder()
                    .worldEntity(WorldEntity.builder().worldId(data).build())
                    .reviewEntity(ReviewEntity.builder().reviewId(returnedReview.getReviewId()).build())
                    .build();
            todoDeleteEntities.add(mappingEntity);
        });

        reviewWorldPlaceMappingRepository.deleteAll(todoDeleteEntities);


        //생성, 수정 할 월드 ID 저장 로직
        List<ReviewWorldMappingEntity> reviewWorldMappingEntities = new ArrayList<>();

        if (reviewDto.getWorldList() != null) {
            reviewDto.getWorldList().forEach(data -> {
                ReviewWorldMappingEntity mapping = ReviewWorldMappingEntity.builder()
                        .worldEntity(WorldEntity.builder().worldId(data).build())
                        .reviewEntity(ReviewEntity.builder().reviewId(returnedReview.getReviewId()).build())
                        .build();
                reviewWorldMappingEntities.add(mapping);
            });
            reviewWorldPlaceMappingRepository.saveAll(reviewWorldMappingEntities);
        }
        ReviewDto result;

        if (returnedReview.getImageUrl() == null) {
            result = ReviewDto.builder()
                    .userSuid(CryptUtils.AES_Encode(returnedReview.getUserEntity().getSuid()))
                    .content(reviewDto.getContent())
                    .reviewId(returnedReview.getReviewId())
                    .build();
        } else {
            result = ReviewDto.builder()
                    .userSuid(CryptUtils.AES_Encode(returnedReview.getUserEntity().getSuid()))
                    .content(reviewDto.getContent())
                    .imageUrls(entity.getImageUrl().split(","))
                    .reviewId(returnedReview.getReviewId())
                    .build();
        }
        return result;
    }

    @Override
    public void deleteReview(Long reviewId) throws YOPLEServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
        try {
            if (!userInfoDto.getSuid().equals(reviewRepo.findByReviewId(reviewId).getUserEntity().getSuid())) {
                throw new YOPLEServiceException(ApiStatusCode.FORBIDDEN);
            }
            reviewWorldPlaceMappingRepository.deleteAllByReviewEntity(ReviewEntity.builder().reviewId(reviewId).build());
            reviewRepo.deleteById(reviewId);
        } catch (Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
    }

    @Override
    public ReviewDto.ReviewWithInviterDto getReview(Long reviewId, Long worldId) throws YOPLEServiceException {
        ReviewDto.ReviewWithInviterDto reviewDto;
        try {
            reviewDto = reviewRepo.qFindReview(reviewId, worldId);
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return reviewDto;
    }

    @Override
    public List<ReviewDto.MyReview> myReviews() throws YOPLEServiceException {
        return reviewRepo.qFindMyReviewsBySuid();

    }

    @Override
    public List<PlaceDto.PlaceSimpleDto> worldPinPlaceInRange(PlaceRangeDto placeRangeDto) throws YOPLEServiceException {
        try {
            List<PlaceDto.PlaceSimpleDto> result = reviewWorldPlaceMappingRepository.findRangePlaces(placeRangeDto);
            if (result.isEmpty()) {
                throw new YOPLEServiceException(ApiStatusCode.CONTENT_NOT_FOUND);
            }
            return result;
        } catch (YOPLEServiceException e) {
            throw e;
        }
    }

    @Override
    public PlaceDetailDto placeDetail(String placeId, Long worldId) throws YOPLEServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        PlaceDto placeDto;
        List<PlaceDetailDto.PlaceDetailInReview> placeDetailInReview;
        PlaceDetailDto result;
        try {
            PlaceEntity placeEntity = placeRepo.findByPlaceId(placeId);
            if (placeEntity == null) {
                throw new YOPLEServiceException(ApiStatusCode.CONTENT_NOT_FOUND);
            }
            placeDto = PlaceDto.builder()
                    .placeId(placeEntity.getPlaceId())
                    .name(placeEntity.getName())
                    .address(placeEntity.getAddress())
                    .roadAddress(placeEntity.getRoadAddress())
                    .categoryGroupCode(placeEntity.getCategoryGroupCode())
                    .categoryGroupName(placeEntity.getCategoryGroupName())
                    .x(placeEntity.getX())
                    .y(placeEntity.getY())
                    .build();

            placeDetailInReview = placeRepo.findPlaceDetailInReview(worldId, placeId, userInfoDto.getSuid());
            placeDetailInReview.sort(new PlaceDetailDto.PlaceDetailInReview.PlaceDetailInReviewComparatorByImageNum());

            boolean isExistMyReview = reviewRepo.existsByUserEntityAndPlaceEntity(
                    UserEntity.builder().suid(userInfoDto.getSuid()).build(),
                    PlaceEntity.builder().placeId(placeId).build());



            result = PlaceDetailDto.builder()
                    .place(placeDto)
                    .reviews(placeDetailInReview)
                    .isExistMyReview(isExistMyReview ? 'Y' : 'N').build();

        } catch (YOPLEServiceException e) {
            throw e;
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {YOPLETransactionException.class})
    public void addEmoji(Long reviewId, Long worldId, Long emojiId) throws YOPLETransactionException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
        try {

            EmojiEntity emojiEntity = emojiRepo.findByEmojiId(EmojiType.findId(emojiId));
            if (!emojiEntity.getEmojiStatus().equals(EmojiType.findActiveType(EmojiType.findId(emojiId).getActiveType()))) {
                throw new YOPLETransactionException(ApiStatusCode.NOT_USABLE_EMOJI);
            }
            if (emojiStatusRepo.existsByUserSuidAndWorldIdAndReviewIdAndEmojiEntity(userInfoDto.getSuid(), worldId, reviewId, emojiEntity)) {
                throw new YOPLETransactionException(ApiStatusCode.ALREADY_EMOJI_ADDED);
            }

            if (!emojiStatusRepo.existsByUserSuidAndWorldIdAndReviewId(userInfoDto.getSuid(), worldId, reviewId)) {
                String reviewOwnerFcmToken = reviewRepo.qFindReviewOwnerFcmToken(reviewId);
                fcmService.sendNotificationToken(reviewOwnerFcmToken, FCMConstant.MSGType.C, userInfoDto.getSuid(), worldId, reviewId);
            }

            if (!emojiStatusNotiRepo.existsByUserSuidAndWorldIdAndReviewId(userInfoDto.getSuid(), worldId, reviewId)) {
                EmojiStatusNotiEntity emojiStatusNotiEntity = EmojiStatusNotiEntity.builder()
                        .userSuid(userInfoDto.getSuid())
                        .worldId(worldId)
                        .reviewId(reviewId)
                        .build();
                emojiStatusNotiRepo.save(emojiStatusNotiEntity);
            }


            EmojiStatusEntity emojiStatusEntity = EmojiStatusEntity.builder()
                    .reviewId(reviewId)
                    .userSuid(userInfoDto.getSuid())
                    .worldId(worldId)
                    .emojiId(emojiEntity.getEmojiId().getId())
                    .build();
            emojiStatusRepo.save(emojiStatusEntity);

        } catch (YOPLETransactionException e) {
            throw e;
        } catch (InterruptedException e) {
            throw new YOPLETransactionException(ApiStatusCode.PARAMETER_CHECK_FAILED);
        } catch (Exception e) {
            throw new YOPLETransactionException(ApiStatusCode.SYSTEM_ERROR);
        }
    }

    @Override
    public ReviewDto.preReview getPreReview(Long reviewId) {
        return reviewRepo.qFindPreReview(reviewId);
    }
}
