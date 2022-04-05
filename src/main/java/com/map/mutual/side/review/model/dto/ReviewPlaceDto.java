package com.map.mutual.side.review.model.dto;

import lombok.*;
/**
 * fileName       : ReviewPlaceDto
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
/**
 * 플레이스, 리뷰를 받기 위한 dto
 * 리뷰 작성/수정을 위해 생성됨.
 */
public class ReviewPlaceDto {
    private ReviewDto review;
    private PlaceDto place;

}
