package com.map.mutual.side.review.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * fileName       : PlaceDto
 * author         : kimjaejung
 * createDate     : 2022/04/05
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/04/05        kimjaejung       최초 생성
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDto {
    @NotNull
    private String placeId;
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

    @QueryProjection
    public PlaceDto(String placeId, String name, BigDecimal x, BigDecimal y) {
        this.placeId = placeId;
        this.name = name;
        this.x = x;
        this.y = y;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlaceSimpleDto {
        private String placeId;
        private String name;
        private BigDecimal x;
        private BigDecimal y;
        private String profilePinUrl;
        private LocalDateTime createDt;
    }
}
