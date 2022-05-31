package com.map.mutual.side.review.model.dto;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * fileName       : ReviewDto
 * author         : kimjaejung
 * createDate     : 2022/03/22
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/22        kimjaejung       최초 생성
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private String userSuid;
    private Long worldId;
    @Size(min = 1, max = 500)
    private String content;
    @Size(min = 0, max = 9)
    private String[] imageUrls;
    private Long reviewId;
    private String placeId;
    @Size(min = 1)
    private List<Long> worldList;

    @QueryProjection
    public ReviewDto(UserEntity userEntity, String content, String imageUrls, Long reviewId) {
        this.userSuid = userEntity.getSuid();
        this.content = content;
        if (imageUrls != null) {
            this.imageUrls = imageUrls.split(",");
        } else {
            this.imageUrls = new String[0];
        }
        this.reviewId = reviewId;
    }

    @QueryProjection
    public ReviewDto(String imageUrls, Long reviewId) {
        if (imageUrls != null) {
            this.imageUrls = imageUrls.split(",");
        } else {
            this.imageUrls = new String[0];
        }
        this.reviewId = reviewId;
    }


    @Builder
    @AllArgsConstructor
    @Getter
    public static class MyReview {
        private Long reviewId;
        private String imageUrl;
        private String placeName;
        private String createDt;

        @QueryProjection
        public MyReview(Long reviewId, String imageUrls, String placeName, LocalDateTime createDt) {
            this.reviewId = reviewId;
            if (imageUrls != null) {
                if (imageUrls.split(",").length >= 2) {
                    this.imageUrl = imageUrls.split(",")[0];
                } else {
                    this.imageUrl = imageUrls;
                }
            }
            this.placeName = placeName;
            this.createDt = LocalDate.of(createDt.getYear(), createDt.getMonth(), createDt.getDayOfMonth()).toString();
        }
    }

    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ReviewWithInviterDto {
        private Long reviewId;
        private String userId;
        private String inviterUserId;
        private String content;
        private String[] imageUrls;
        private String createDt;

        private List<TempEmoji> emoji;

        @AllArgsConstructor
        @Builder
        @NoArgsConstructor
        @Getter
        public static class TempEmoji {
            private Long emojiType;
            private Long typeQuantity;
            private boolean isChecked;
            private LocalDateTime createdDt;
        }

        @QueryProjection
        public ReviewWithInviterDto(Long reviewId, String content, String imageUrls, String userId, String inviterUserId, LocalDateTime createDt) {
            this.reviewId = reviewId;
            this.userId = userId;
            if(inviterUserId == null || inviterUserId.equals("")) {
                this.inviterUserId = " ";
            } else {
                this.inviterUserId = inviterUserId;
            }
            this.content = content;
            if (imageUrls != null) {
                this.imageUrls = imageUrls.split(",");
            } else {
                this.imageUrls = new String[0];
            }
            this.createDt = LocalDate.of(createDt.getYear(), createDt.getMonth(), createDt.getDayOfMonth()).toString();

        }

    }

    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @Getter
    @Setter
    public static class preReview {
        private Long reviewId;
        private String placeId;
        private String placeName;
        private String placeAddress;
        private String placeRoadAddress;
        private List<WorldDto> worldList;
        private String[] imageUrls;
        private String content;

        @QueryProjection
        public preReview(Long reviewId, String placeId, String placeName, String placeAddress, String placeRoadAddress, String imageUrls, String content) {
            this.reviewId = reviewId;
            this.placeId = placeId;
            this.placeName = placeName;
            this.placeAddress = placeAddress;
            this.placeRoadAddress = placeRoadAddress;
            if (imageUrls != null) {
                this.imageUrls = imageUrls.split(",");
            } else {
                this.imageUrls = new String[0];
            }
            this.content = content;
        }
    }
}
