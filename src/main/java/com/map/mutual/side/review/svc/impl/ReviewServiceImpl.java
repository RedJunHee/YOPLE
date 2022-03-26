package com.map.mutual.side.review.svc.impl;

import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.review.model.dto.ReviewDto;
import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.map.mutual.side.review.repository.ReviewRepository;
import com.map.mutual.side.review.svc.ReviewService;
import com.map.mutual.side.world.repository.WorldRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;
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
    private ReviewRepository reviewRepository;
    @Autowired
    private WorldRepo worldRepo;
    @Autowired
    private UserInfoRepo userInfoRepo;

    @Override
    public void createUpdateReview(ReviewDto reviewDto) {

        try {
            ReviewEntity reviewEntity = ReviewEntity.builder()
                    .title(reviewDto.getTitle())
                    .content(reviewDto.getContent())
//                    .imageUrl(reviewDto.getImageUrls().stream().map(String::toString).collect(Collectors.joining(",")))
                    .build();

            reviewRepository.save(reviewEntity);
        } catch (YOPLEServiceException e) {
            throw e;
        }
    }

    @Override
    public ReviewDto getReview(Long reviewId) {
        ReviewEntity reviewEntity;
        ReviewDto reviewDto;
        try {
            reviewEntity = reviewRepository.findById(reviewId).orElseThrow(NullPointerException::new);
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
}
