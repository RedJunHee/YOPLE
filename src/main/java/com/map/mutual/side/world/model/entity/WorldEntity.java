package com.map.mutual.side.world.model.entity;


import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.common.repository.config.TimeEntity;
import com.map.mutual.side.review.model.entity.ReviewWorldMappingEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * fileName       : WorldEntity
 * author         : kimjaejung
 * createDate     : 2022/03/17
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/17        kimjaejung       최초 생성
 *
 */
@Entity
@Table(name = "WORLD")
@NoArgsConstructor
@Getter
@DynamicUpdate
@Builder
@AllArgsConstructor
@Setter
public class WorldEntity extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="WORLD_ID",nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long worldId;

//    @Id
    @Column(name = "WORLD_OWNER", insertable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String worldOwner;

    //월드 명
    @Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(60)")
    private String worldName;

    //월드 내용
    @Column(name = "DESCRIPTION", nullable = false, columnDefinition = "VARCHAR(800)")
    private String worldDesc;

    public void updateWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void updateWorldDesc(String worldDesc) {
        this.worldDesc = worldDesc;
    }
}
