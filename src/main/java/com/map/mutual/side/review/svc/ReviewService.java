package com.map.mutual.side.review.svc;

import com.map.mutual.side.review.model.dto.ReviewDto;

import java.util.List;

/**
 * fileName       : ReviewService
 * author         : kimjaejung
 * createDate     : 2022/03/22
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/22        kimjaejung       최초 생성
 *
 */
public interface ReviewService {
    void createReview(ReviewDto reviewDto);
    void updateReview(ReviewDto reviewDto);
    void createReviewWorldMapping(Long worldId, Long reviewId);
    void deleteReview(Long reviewId);
    ReviewDto getReview(Long reviewDto);
    List<ReviewDto> getReviews(Long worldId);
    List<ReviewDto> myReviews(String userSuid);
}
