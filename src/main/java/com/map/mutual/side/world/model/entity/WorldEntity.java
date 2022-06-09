package com.map.mutual.side.world.model.entity;


import com.map.mutual.side.common.repository.config.TimeEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

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
public class WorldEntity extends TimeEntity implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="WORLD_ID",nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long worldId;

    @Column(name = "WORLD_OWNER", nullable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String worldOwner;

    //월드 명
    @Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(50)")
    @Size(min = 1 , max = 25)
    private String worldName;

    //월드 내용
    @Column(name = "DESCRIPTION", nullable = false, columnDefinition = "VARCHAR(160)")
    @Size(min = 1 , max = 80)
    private String worldDesc;

    public void updateWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void updateWorldDesc(String worldDesc) {
        this.worldDesc = worldDesc;
    }

    @Override
    public Long getId() {
        return worldId;
    }

    @Override
    public boolean isNew() {
        return getCreateTime() == null;
    }

    @Override
    public String toString() {
        return "WorldEntity{" +
                "worldId=" + worldId +
                ", worldOwner='" + worldOwner + '\'' +
                ", worldName='" + worldName + '\'' +
                ", worldDesc='" + worldDesc + '\'' +
                '}';
    }
}
