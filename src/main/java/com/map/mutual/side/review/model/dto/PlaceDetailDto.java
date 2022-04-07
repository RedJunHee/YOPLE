package com.map.mutual.side.review.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
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
    public static class TempReview {
        private Long reviewId;
        private String title;
        private String content;
        private String[] imageUrls;
        private String userSuid;
        private String invitingUserSuid;

        @QueryProjection
        public TempReview(Long reviewId, String title, String content, String imageUrls, String userSuid, String invitingUserSuid, LocalDateTime updateTime) {
            this.reviewId = reviewId;
            this.title = title;
            this.content = content;
            if (imageUrls != null) {
                this.imageUrls = imageUrls.split(",");
            } else {
                this.imageUrls = new String[0];
            }
            this.userSuid = userSuid;
            this.invitingUserSuid = invitingUserSuid;
        }
    }
}
