package com.map.mutual.side.review.svc.impl;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
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
    @Transactional(rollbackFor = {Exception.class})
    public ReviewDto createReview(ReviewPlaceDto dto) throws YOPLEServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
        ReviewDto result;

        try {
            ReviewEntity reviewEntity = ReviewEntity.builder()
                    .userEntity(UserEntity.builder().suid(userInfoDto.getSuid()).build())
                    .content(dto.getReview().getContent())
                    .placeEntity(PlaceEntity.builder().placeId(dto.getPlace().getPlaceId()).build())
//                    .imageUrl(reviewDto.getImageUrls().stream().map(String::toString).collect(Collectors.joining(",")))
                    .build();
            result = saveReviewAndMappings(dto.getReview(), reviewEntity, dto.getPlace());
        } catch (Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
        return result;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ReviewDto updateReview(ReviewDto reviewDto) throws YOPLEServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
        ReviewDto result;
        try {
            ReviewEntity entity = reviewRepo.findByReviewId(reviewDto.getReviewId());
            if (entity == null) {
                throw new YOPLEServiceException(ApiStatusCode.CONTENT_NOT_FOUND);
            } else if (!entity.getUserEntity().getSuid().equals(userInfoDto.getSuid())) {
                throw new YOPLEServiceException(ApiStatusCode.FORBIDDEN);
            } else {
                entity.setContent(reviewDto.getContent());
                result = saveReviewAndMappings(reviewDto, entity, null);
            }
        }  catch (Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
        return result;
    }

    public ReviewDto saveReviewAndMappings(ReviewDto reviewDto, ReviewEntity entity, @Nullable PlaceDto placeDto) throws YOPLEServiceException {
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
        try {
            result = ReviewDto.builder()
                    .userSuid(CryptUtils.AES_Encode(returnedReview.getUserEntity().getSuid()))
                    .content(reviewDto.getContent())
//                .imageFiles()
                    .reviewId(returnedReview.getReviewId())
                    // TODO: 2022/03/30 월드 리스트 반환여부 , image 관련 처리
                    .build();
        } catch (Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
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
            reviewDto = reviewRepo.findByReviewWithInviter(reviewId, worldId);
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return reviewDto;
    }

    @Override
    public List<ReviewDto> myReviews() throws YOPLEServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        List<ReviewEntity> reviewEntity;
        List<ReviewDto> reviewDto = new ArrayList<>();
        try {
            reviewEntity = reviewRepo.findAllByUserEntity(UserEntity.builder().suid(userInfoDto.getSuid()).build());
            reviewEntity.forEach(data -> {
                        try {
                            reviewDto.add(ReviewDto.builder()
                                            .reviewId(data.getReviewId())
                                            .userSuid(CryptUtils.AES_Encode(data.getUserEntity().getSuid()))
                                            .content(data.getContent())
                                            // TODO: 2022/03/29 imageUrl 추가해야함
                //                  .imageUrls()
                                            .build());
                        } catch (Exception e) {
                            try {
                                throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
                            } catch (YOPLEServiceException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
            );
        }  catch (Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
        return reviewDto;
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

            placeDetailInReview = placeRepo.findPlaceDetails(worldId, placeId);
            placeDetailInReview.sort(new PlaceDetailDto.PlaceDetailInReview.PlaceDetailInReviewComparatorByImageNum());


            result = PlaceDetailDto.builder()
                    .place(placeDto)
                    .reviews(placeDetailInReview).build();
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void addEmoji(Long reviewId, Long worldId, Long emojiId) throws YOPLEServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
        try {

            EmojiEntity emojiEntity = emojiRepo.findByEmojiId(EmojiType.findId(emojiId));
            if (!emojiEntity.getEmojiStatus().equals(EmojiType.findActiveType(EmojiType.findId(emojiId).getActiveType()))) {
                throw new YOPLEServiceException(ApiStatusCode.NOT_USABLE_EMOJI);
            }
            if (emojiStatusRepo.existsByUserSuidAndWorldIdAndReviewIdAndEmojiEntity(userInfoDto.getSuid(), worldId, reviewId, emojiEntity)) {
                throw new YOPLEServiceException(ApiStatusCode.ALREADY_EMOJI_ADDED);
            }

            if(!emojiStatusRepo.existsByUserSuidAndWorldIdAndReviewId(userInfoDto.getSuid(), worldId, reviewId)){
                String reviewOwnerFcmToken = reviewRepo.findByReviewOwnerFcmToken(reviewId);
                fcmService.sendNotificationToken(reviewOwnerFcmToken, FCMConstant.MSGType.C, userInfoDto.getSuid(), worldId, reviewId);
            }

            if(!emojiStatusNotiRepo.existsByUserSuidAndWorldIdAndReviewId(userInfoDto.getSuid(), worldId, reviewId)) {
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

        } catch (YOPLEServiceException e) {
            throw e;
        } catch (InterruptedException e) {
            throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED);
        } catch (Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
    }


}
