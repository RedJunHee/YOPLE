package com.map.mutual.side.common.repository.config;

import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
/**
 * fileName       : UpdateDtEntity
 * author         : kimjaejung
 * createDate     : 2022/03/25
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/25        kimjaejung       최초 생성
 *
 */
@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
public class UpdateDtEntity {
    @LastModifiedDate
    @Column(name = "UPDATE_DT", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime updateTime;
}
