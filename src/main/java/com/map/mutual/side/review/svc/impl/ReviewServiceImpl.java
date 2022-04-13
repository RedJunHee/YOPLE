package com.map.mutual.side.review.svc.impl;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.enumerate.BooleanType;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.review.model.dto.*;
import com.map.mutual.side.review.model.entity.*;
import com.map.mutual.side.review.model.enumeration.EmojiType;
import com.map.mutual.side.review.repository.*;
import com.map.mutual.side.review.svc.ReviewService;
import com.map.mutual.side.world.model.entity.WorldEntity;
import com.map.mutual.side.world.repository.WorldRepo;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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
    private WorldRepo worldRepo;
    @Autowired
    private UserInfoRepo userInfoRepo;
    @Autowired
    private ReviewWorldMappingRepository reviewWorldPlaceMappingRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PlaceRepo placeRepo;
    @Autowired
    private EmojiStatusRepo emojiStatusRepo;
    @Autowired
    private EmojiRepo emojiRepo;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ReviewDto createReview(ReviewPlaceDto dto) {
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
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return result;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ReviewDto updateReview(ReviewDto reviewDto) {
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
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return result;
    }

    public ReviewDto saveReviewAndMappings(ReviewDto reviewDto, ReviewEntity entity, @Nullable PlaceDto placeDto) {
        ReviewEntity returnedReview;
        try {
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
        } catch (YOPLEServiceException e) {
            throw e;
        }

        ReviewDto result = ReviewDto.builder()
                .userSuid(returnedReview.getUserEntity().getSuid())
                .content(reviewDto.getContent())
//                .imageFiles()
                .reviewId(returnedReview.getReviewId())
                // TODO: 2022/03/30 월드 리스트 반환여부 , image 관련 처리
                .build();
        return result;
    }

    @Override
    public void deleteReview(Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
        try {
            if (!userInfoDto.getSuid().equals(reviewRepo.findByReviewId(reviewId).getUserEntity().getSuid())) {
                throw new YOPLEServiceException(ApiStatusCode.FORBIDDEN);
            }
            reviewWorldPlaceMappingRepository.deleteAllByReviewEntity(ReviewEntity.builder().reviewId(reviewId).build());
            reviewRepo.deleteById(reviewId);
        } catch (YOPLEServiceException e) {
            throw e;
        }
    }

    @Override
    public ReviewDto.ReviewWithInviterDto getReview(Long reviewId, Long worldId) {
        ReviewDto.ReviewWithInviterDto reviewDto;
        try {
            reviewDto = reviewRepo.findByReviewWithInviter(reviewId, worldId);
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return reviewDto;
    }

    @Override
    public List<ReviewDto> myReviews() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        List<ReviewEntity> reviewEntity;
        List<ReviewDto> reviewDto = new ArrayList<>();
        try {
            reviewEntity = reviewRepo.findAllByUserEntity(UserEntity.builder().suid(userInfoDto.getSuid()).build());
            reviewEntity.forEach(data -> reviewDto.add(ReviewDto.builder()
                            .reviewId(data.getReviewId())
                            .userSuid(data.getUserEntity().getSuid())
                            .content(data.getContent())
                            // TODO: 2022/03/29 imageUrl 추가해야함
//                  .imageUrls()
                            .build())
            );
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return reviewDto;
    }

    @Override
    public List<PlaceDto.PlaceSimpleDto> worldPinPlaceInRange(PlaceRangeDto placeRangeDto) {
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
    public PlaceDetailDto placeDetail(String placeId, Long worldId) {
        PlaceDto placeDto;
        List<PlaceDetailDto.TempReview> tempReview;
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

            tempReview = placeRepo.findPlaceDetails(worldId, placeId);

            tempReview.sort(new PlaceDetailDto.TempReview.TempReviewComparatorByImageNum());


            result = PlaceDetailDto.builder()
                    .place(placeDto)
                    .reviews(tempReview).build();
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return result;
    }

    @Override
    public void addEmoji(Long reviewId, Long worldId, EmojiType emojiType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
        try {
            EmojiEntity emojiEntity = emojiRepo.findByEmojiValue(emojiType);
            if(!emojiEntity.getEmojiStatus().equals(BooleanType.Y)) {
                throw new YOPLEServiceException(ApiStatusCode.NOT_USABLE_EMOJI);
            }
            if(emojiStatusRepo.existsByUserSuidAndWorldIdAndReviewIdAndEmojiEntity(userInfoDto.getSuid(), worldId, reviewId, emojiEntity)){
                throw new YOPLEServiceException(ApiStatusCode.ALREADY_EMOJI_ADDED);
            }
            EmojiStatusEntity emojiStatusEntity = EmojiStatusEntity.builder()
                    .reviewId(reviewId)
                    .userSuid(userInfoDto.getSuid())
                    .worldId(worldId)
                    .emojiEntity(emojiEntity)
                    .build();
            emojiStatusRepo.save(emojiStatusEntity);
        } catch (YOPLEServiceException e) {
            throw e;
        }
    }



}
