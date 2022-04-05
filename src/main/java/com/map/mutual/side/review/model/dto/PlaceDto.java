package com.map.mutual.side.review.model.dto;

import lombok.*;

import java.math.BigDecimal;
/**
 * fileName       : PlaceDto
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
public class PlaceDto {
    private Long placeId;
    private String name;
    private String address;
    private String roadAddress;
    private String categoryGroupCode;
    private String categoryGroupName;
    private BigDecimal x;
    private BigDecimal y;
}
