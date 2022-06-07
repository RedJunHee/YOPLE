package com.map.mutual.side.auth.model.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class       : UserReportDto
 * Author      : 조 준 희
 * Description : 사용자 신고 Dto
 * History     : [2022-04-21] - 조 준희 - Class Create
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReportDto {
    private String reportSuid;
    private String reportTitle;
    private String reportDesc;

    public void suidChange(String decodingSuid){
        reportSuid = decodingSuid;
    }

    @Override
    public String toString() {
        return "UserReportDto{" +
                "reportSuid='" + reportSuid + '\'' +
                ", reportTitle='" + reportTitle + '\'' +
                ", reportDesc='" + reportDesc + '\'' +
                '}';
    }
}
