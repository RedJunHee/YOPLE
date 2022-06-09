package com.map.mutual.side.auth.model.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class       : ReviewReportDto
 * Author      : 조 준 희
 * Description : 리뷰 신고 Dto
 * History     : [2022-04-21] - 조 준희 - Class Create
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReportDto {
    private Long reviewId;
    private String reportTitle;
    private String reportDesc;

    @Override
    public String toString() {
        return "ReviewReportDto{" +
                "reviewId=" + reviewId +
                ", reportTitle='" + reportTitle + '\'' +
                ", reportDesc='" + reportDesc + '\'' +
                '}';
    }
}
