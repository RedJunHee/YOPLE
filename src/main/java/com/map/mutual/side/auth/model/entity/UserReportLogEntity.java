package com.map.mutual.side.auth.model.entity;

import com.map.mutual.side.common.repository.config.CreateDtEntity;
import com.map.mutual.side.common.repository.config.TimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

/**
 * Class       : UserReportLogEntity
 * Author      : 조 준 희
 * Description : 사용자 신고 이력 테이블
 * History     : [2022-04-21] - 조 준희 - Class Create
 */
@Entity
@Table(name = "USER_REPORT_LOG")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReportLogEntity extends CreateDtEntity implements Persistable<Long> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private Long seq;

    @Column(name = "USER_SUID", nullable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Column(name = "REPORT_SUID", nullable = false, columnDefinition = "VARCHAR(18)")
    private String reportSuid;

    @Column(name = "REPORT_TITLE", nullable = false, columnDefinition = "VARCHAR(100)")
    private String reportTitle;

    @Column(name = "REPORT_DESC", nullable = false, columnDefinition = "VARCHAR(500)")
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
