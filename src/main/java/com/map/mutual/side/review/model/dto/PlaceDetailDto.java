package com.map.mutual.side.review.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * fileName       : PlaceDetailDto
 * author         : kimjaejung
 * createDate     : 2022/04/05
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/04/05        kimjaejung       최초 생성
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDetailDto {

    private List<PlaceDetailInReview> reviews;
    private PlaceDto place;
    private char isExistMyReview;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlaceDetailInReview {
        private Long reviewId;
        private String userId;
        private String[] imageUrls;
        private String profileUrl;
        private String profilePinUrl;

        @QueryProjection
        public PlaceDetailInReview(Long reviewId, String imageUrls, String profileUrl, String profilePinUrl, LocalDateTime createDt, String userId) {
            this.reviewId = reviewId;
            this.profileUrl = profileUrl;
            this.profilePinUrl = profilePinUrl;
            if (imageUrls != null) {
                this.imageUrls = imageUrls.split(",");
            } else {
                this.imageUrls = new String[0];
            }
            this.userId = userId;
        }

        public static class PlaceDetailInReviewComparatorByImageNum implements Comparator<PlaceDetailInReview> {
            @Override
            public int compare(PlaceDetailInReview o1, PlaceDetailInReview o2) {
                return Integer.compare(o2.imageUrls.length, o1.imageUrls.length);
            }
        }
    }
}
