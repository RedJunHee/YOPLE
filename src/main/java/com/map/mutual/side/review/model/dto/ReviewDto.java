package com.map.mutual.side.review.model.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {
    private String userSuid;
    private Long worldId;
    private String title;
    private String content;
    private MultipartFile[] imageFiles;
    private String[] imageUrls;
    private Long reviewId;
}
