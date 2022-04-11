package com.map.mutual.side.review.svc;

import com.map.mutual.side.review.model.dto.*;
import com.map.mutual.side.review.model.enumeration.EmojiType;

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
    ReviewDto updateReview(ReviewDto reviewDto);
    void deleteReview(Long reviewId);
    ReviewDto getReview(Long reviewDto);
    List<ReviewDto> getReviews(Long worldId);
    List<ReviewDto> myReviews();
    List<PlaceDto.PlaceSimpleDto> worldPinAllPlace(Long worldId);
    List<PlaceDto.PlaceSimpleDto> worldPinPlaceInRange(PlaceRangeDto placeRangeDto);
    PlaceDetailDto placeDetail(String placeId, Long worldId);
    void addEmoji(Long reviewId, Long worldId, EmojiType emojiType);
}
