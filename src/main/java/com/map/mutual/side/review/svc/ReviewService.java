package com.map.mutual.side.review.svc;

import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.review.model.dto.*;

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
    ReviewDto createReview(ReviewPlaceDto dto) throws Exception;
    ReviewDto updateReview(ReviewDto reviewDto) throws YOPLEServiceException;
    void deleteReview(Long reviewId) throws YOPLEServiceException;
    ReviewDto.ReviewWithInviterDto getReview(Long reviewId, Long worldId) throws YOPLEServiceException;
    List<ReviewDto.MyReview> myReviews() throws YOPLEServiceException;
    List<PlaceDto.PlaceSimpleDto> worldPinPlaceInRange(PlaceRangeDto placeRangeDto) throws YOPLEServiceException;
    PlaceDetailDto placeDetail(String placeId, Long worldId) throws YOPLEServiceException;
    void addEmoji(Long reviewId, Long worldId, Long emojiId) throws YOPLEServiceException;
}
