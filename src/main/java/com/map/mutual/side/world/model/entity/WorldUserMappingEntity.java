package com.map.mutual.side.world.model.entity;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.world.model.keys.WorldUserMappingEntityKeys;
import lombok.*;

import javax.persistence.*;


@Entity
@Table(name = "WORLD_USER_MAPPING")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@IdClass(WorldUserMappingEntityKeys.class)
public class WorldUserMappingEntity {
    @Id
    @Column(name="USER_SUID", nullable = false, insertable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Id
    @Column(name = "WORLD_ID", nullable = false, insertable = false, updatable = false, columnDefinition = "BIGINT")
    private Long worldId;

    @Column(name = "WORLD_USER_CODE", nullable = false, updatable = false, columnDefinition = "CHAR(6)")
    private String worldUserCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_SUID", referencedColumnName = "SUID")
    private UserEntity userEntity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORLD_ID", referencedColumnName = "WORLD_ID")
    private WorldEntity worldEntity;
}
