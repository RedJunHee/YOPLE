package com.map.mutual.side.review.model.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
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
    @NotNull
    private Long placeId;
    @NotNull
    private String name;
    @NotNull
    private String address;
    @NotNull
    private String roadAddress;
    @NotNull
    private String categoryGroupCode;
    @NotNull
    private String categoryGroupName;
    @NotNull
    private BigDecimal x;
    @NotNull
    private BigDecimal y;
}
