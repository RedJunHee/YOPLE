package com.map.mutual.side.review.svc;

import com.map.mutual.side.review.model.dto.ReviewDto;
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
    void createUpdateReview(ReviewDto reviewDto);
    ReviewDto getReview(Long reviewDto);
}
