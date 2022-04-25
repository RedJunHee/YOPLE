package com.map.mutual.side.auth.model.entity;

import com.map.mutual.side.common.repository.config.CreateDtEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

/**
 * Class       : ReviewReportLogEntity
 * Author      : 조 준 희
 * Description : 리뷰 신고 이력 테이블
 * History     : [2022-04-21] - 조 준희 - Class Create
 */
@Entity
@Table(name = "REVIEW_REPORT_LOG")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReportLogEntity extends CreateDtEntity implements Persistable<Long> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private Long seq;

    @Column(name = "USER_SUID", nullable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Column(name = "REVIEW_ID", nullable = false, columnDefinition = "BIGINT")
    private Long reviewId;

    @Column(name = "REPORT_TITLE", nullable = false, columnDefinition = "VARCHAR(100)")
    private String reportTitle;

    @Column(name = "REPORT_DESC", nullable = false, columnDefinition = "VARCHAR(1000)")
    private String reportDesc;

    @Override
    public Long getId() {
        return seq;
    }

    @Override
    public boolean isNew() {
        return getCreateTime() == null;
    }
}
