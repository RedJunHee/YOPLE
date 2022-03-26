package com.map.mutual.side.common.repository.config;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.time.LocalDateTime;
/**
 * fileName       : CreateDtEntity
 * author         : kimjaejung
 * createDate     : 2022/03/25
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/25        kimjaejung       최초 생성
 *
 */
@MappedSuperclass
public class CreateDtEntity {
    @Column(name = "CREATE_DT")
    private LocalDateTime createTime;
    @PrePersist
    public void before() {
        LocalDateTime now = LocalDateTime.now();
        if (createTime == null) {
            this.createTime = now;
        }
    }
}
