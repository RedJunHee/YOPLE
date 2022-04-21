package com.map.mutual.side.auth.model.entity;

import com.map.mutual.side.common.repository.config.TimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

/**
 * Class       : UserBlockLogEntity
 * Author      : 조 준 희
 * Description : 사용자 차단 이력 테이블
 * History     : [2022-04-21] - 조 준희 - Class Create
 */
@Entity
@Table(name = "USER_BLOCK_LOG")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBlockLogEntity extends TimeEntity implements Persistable<Long> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private Long seq;

    @Column(name = "USER_SUID", nullable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Column(name = "BLOCK_SUID", nullable = false, columnDefinition = "VARCHAR(18)")
    private String blockSuid;

    @Column(name = "IS_BLOCKING", columnDefinition = "CHAR(1)")
    @ColumnDefault(value = "'Y'")
    private String isBlocking;

    @Override
    public Long getId() {
        return seq;
    }

    @Override
    public boolean isNew() {
        return getCreateTime() == null;
    }

    public void blockCancel(){
        isBlocking = "N";
    }
}
