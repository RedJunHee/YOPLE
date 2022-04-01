package com.map.mutual.side.review.svc.impl;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.review.model.dto.ReviewDto;
import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.map.mutual.side.review.model.entity.ReviewWorldMappingEntity;
import com.map.mutual.side.review.repository.ReviewRepo;
import com.map.mutual.side.review.repository.ReviewWorldMappingRepository;
import com.map.mutual.side.review.svc.ReviewService;
import com.map.mutual.side.world.model.entity.WorldEntity;
import com.map.mutual.side.world.repository.WorldRepo;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ReviewWorldMappingRepository reviewWorldMappingRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ReviewDto createReview(ReviewDto reviewDto) {
        ReviewDto result;
        try {
            ReviewEntity reviewEntity = ReviewEntity.builder()
                    .userEntity(UserEntity.builder().suid(reviewDto.getUserSuid()).build())
                    .title(reviewDto.getTitle())
                    .content(reviewDto.getContent())
//                    .imageUrl(reviewDto.getImageUrls().stream().map(String::toString).collect(Collectors.joining(",")))
                    .build();
            result = saveReviewAndMappings(reviewDto, reviewEntity);
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return result;
    }


    @Override
    public ReviewDto updateReview(ReviewDto reviewDto) {
        ReviewDto result;
        try {
            ReviewEntity entity = reviewRepo.findByReviewId(reviewDto.getReviewId());
            if (entity == null) {
                throw new YOPLEServiceException(ApiStatusCode.CONTENT_NOT_FOUND);
            } else {
                entity.setContent(reviewDto.getContent());
                entity.setTitle(reviewDto.getTitle());
                result = saveReviewAndMappings(reviewDto, entity);
            }
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return result;
    }

    private ReviewDto saveReviewAndMappings(ReviewDto reviewDto, ReviewEntity entity) {
        ReviewEntity returnedReview;
        try {
            returnedReview = reviewRepo.save(entity);
            //공통 리뷰 저장

            List<Long> presentWorldIds = reviewWorldMappingRepository.findAllByReviewEntity(ReviewEntity.builder().reviewId(reviewDto.getReviewId()).build())
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
                        .reviewEntity(ReviewEntity.builder().reviewId(reviewDto.getReviewId()).build())
                        .build();
                todoDeleteEntities.add(mappingEntity);
            });

            reviewWorldMappingRepository.deleteAll(todoDeleteEntities);



            //생성, 수정 할 월드 ID 저장 로직
            List<ReviewWorldMappingEntity> reviewWorldMappingEntities = new ArrayList<>();

            if(reviewDto.getWorldList() != null) {
                reviewDto.getWorldList().forEach(data -> {
                    ReviewWorldMappingEntity mapping = ReviewWorldMappingEntity.builder()
                            .reviewEntity(ReviewEntity.builder().reviewId(returnedReview.getReviewId()).build())
                            .worldEntity(WorldEntity.builder().worldId(data).build())
                            .build();
                    reviewWorldMappingEntities.add(mapping);
                });
                reviewWorldMappingRepository.saveAll(reviewWorldMappingEntities);
            }
        } catch (YOPLEServiceException e) {
            throw e;
        }

        ReviewDto result = ReviewDto.builder()
                .userSuid(returnedReview.getUserEntity().getSuid())
                .title(returnedReview.getTitle())
                .content(reviewDto.getContent())
//                .imageFiles()
                .reviewId(returnedReview.getReviewId())
                // TODO: 2022/03/30 월드 리스트 반환여부 , image 관련 처리
                .build();
        return result;
    }

    @Override
    public void deleteReview(Long reviewId) {
        try {
            reviewWorldMappingRepository.deleteAllByReviewEntity(ReviewEntity.builder().reviewId(reviewId).build());
            reviewRepo.deleteById(reviewId);
        } catch (YOPLEServiceException e) {
            throw e;
        }
    }

    @Override
    public ReviewDto getReview(Long reviewId) {
        ReviewEntity reviewEntity;
        ReviewDto reviewDto;
        try {
            reviewEntity = reviewRepo.findById(reviewId).orElseThrow(NullPointerException::new);
            reviewDto = ReviewDto.builder()
                    .userSuid(reviewEntity.getUserEntity().getSuid())
                    .title(reviewEntity.getTitle())
                    .content(reviewEntity.getContent())
//                    .imageUrls(Arrays.stream(reviewEntity.getImageUrl().split(",")).collect(Collectors.toList()))
                    .build();
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return reviewDto;
    }

    @Override
    public List<ReviewDto> getReviews(Long worldId) {
        List<ReviewDto> reviewDto;
        try {
            reviewDto = reviewWorldMappingRepository.findAllReviewsByWorldId(worldId);
            if(reviewDto == null) {
                return null;
            }
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return reviewDto;
    }

    @Override
    public List<ReviewDto> myReviews(String userSuid) {
        List<ReviewEntity> reviewEntity;
        List<ReviewDto> reviewDto = new ArrayList<>();
        try {
            reviewEntity = reviewRepo.findAllByUserEntity(UserEntity.builder().suid(userSuid).build());
            reviewEntity.forEach(data -> reviewDto.add(ReviewDto.builder()
                            .reviewId(data.getReviewId())
                            .userSuid(data.getUserEntity().getSuid())
                            .title(data.getTitle())
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
}
