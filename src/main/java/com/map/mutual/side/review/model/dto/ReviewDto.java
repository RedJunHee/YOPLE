package com.map.mutual.side.review.model.dto;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private MultipartFile[] imageFiles;
    private String[] imageUrls;
    private Long reviewId;
    private String placeId;
    @Size(min = 1)
    private List<Long> worldList;


    @QueryProjection
    public ReviewDto(UserEntity userEntity, String content, String imageUrls, Long reviewId) {
        this.userSuid = userEntity.getSuid();
        this.content = content;
        if(imageUrls != null) {
            this.imageUrls = imageUrls.split(",");
        } else {
            this.imageUrls = new String[0];
        }
        this.reviewId = reviewId;
    }

    @QueryProjection
    public ReviewDto(String imageUrls, Long reviewId) {
        if(imageUrls != null) {
            this.imageUrls = imageUrls.split(",");
        } else {
            this.imageUrls = new String[0];
        }
        this.reviewId = reviewId;
    }

    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ReviewWithInviterDto {
        private Long reviewId;
        private String userid;
        private String inviterUserId;
        private String content;
        private String[] imageUrls;
        private String createDt;

        @QueryProjection
        public ReviewWithInviterDto(Long reviewId, String content, String imageUrls , String userid, String inviterUserId, LocalDateTime createDt) {
            this.reviewId = reviewId;
            this.userid = userid;
            this.inviterUserId = inviterUserId;
            this.content = content;
            if(imageUrls != null) {
                this.imageUrls = imageUrls.split(",");
            } else {
                this.imageUrls = new String[0];
            }
            this.createDt = LocalDate.of(createDt.getYear(), createDt.getMonth(), createDt.getDayOfMonth()).toString();

        }
    }
}
