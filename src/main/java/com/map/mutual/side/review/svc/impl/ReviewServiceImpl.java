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

import java.util.List;

/**
 * fileName       : ReviewServiceImpl
 * author         : kimjaejung
 * createDate     : 2022/03/22
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/22        kimjaejung       최초 생성
 *
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
    public void createReview(ReviewDto reviewDto) {

        try {
            ReviewEntity reviewEntity = ReviewEntity.builder()
                    .userEntity(UserEntity.builder().suid(reviewDto.getUserSuid()).build())
                    .title(reviewDto.getTitle())
                    .content(reviewDto.getContent())
//                    .imageUrl(reviewDto.getImageUrls().stream().map(String::toString).collect(Collectors.joining(",")))
                    .build();
            reviewRepo.save(reviewEntity);
        } catch (YOPLEServiceException e) {
            throw e;
        }
    }

    @Override
    public void updateReview(ReviewDto reviewDto) {

        try {
            ReviewEntity entity = reviewRepo.findByReviewId(reviewDto.getReviewId());
            if(entity == null) {
                throw new YOPLEServiceException(ApiStatusCode.CONTENT_NOT_FOUND);
            } else {
                entity.setContent(reviewDto.getContent());
                entity.setTitle(reviewDto.getTitle());
                // TODO: 2022/03/29 setting images
            }
        } catch (YOPLEServiceException e) {
            throw e;
        }
    }

    @Override
    public void createReviewWorldMapping(Long worldId, Long reviewId) {

        try {
            ReviewWorldMappingEntity entity = ReviewWorldMappingEntity.builder()
                    .worldEntity(WorldEntity.builder().worldId(worldId).build())
                    .reviewEntity(ReviewEntity.builder().reviewId(reviewId).build())
                    .build();
            reviewWorldMappingRepository.save(entity);
        } catch (YOPLEServiceException e) {
            throw e;
        }
    }

    @Override
    public void deleteReview(Long reviewId) {
        try {
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
        List<ReviewEntity> reviewEntity;
        List<ReviewDto> reviewDto = null;
        try {
            reviewEntity = reviewWorldMappingRepository.findAllReviewsByWorldId(worldId);
            reviewEntity.stream().map(data -> reviewDto.add(modelMapper.map(data, ReviewDto.class)));
        } catch (YOPLEServiceException e) {
            throw e;
        }
        return reviewDto;
    }
}
