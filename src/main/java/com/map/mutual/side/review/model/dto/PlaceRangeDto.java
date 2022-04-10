package com.map.mutual.side.review.model.dto;

import lombok.*;

import java.math.BigDecimal;
/**
 * fileName       : PlaceRangeDto
 * author         : kimjaejung
 * createDate     : 2022/04/10
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/04/10        kimjaejung       최초 생성
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceRangeDto {
    private Long worldId;
    private BigDecimal x_start;
    private BigDecimal x_end;
    private BigDecimal y_start;
    private BigDecimal y_end;
}
