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

    private List<TempReview> reviews;
    private PlaceDto place;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TempReview{
        private Long reviewId;
        private String[] imageUrls;
        private String profileUrl;

        @QueryProjection
        public TempReview(Long reviewId, String imageUrls, String profileUrl, LocalDateTime createDt) {
            this.reviewId = reviewId;
            this.profileUrl = profileUrl;
            if (imageUrls != null) {
                this.imageUrls = imageUrls.split(",");
            } else {
                this.imageUrls = new String[0];
            }
        }

        public static class TempReviewComparatorByImageNum implements Comparator<TempReview> {
            @Override
            public int compare(TempReview o1, TempReview o2) {
                return Integer.compare(o2.imageUrls.length, o1.imageUrls.length);
            }
        }
    }
}
