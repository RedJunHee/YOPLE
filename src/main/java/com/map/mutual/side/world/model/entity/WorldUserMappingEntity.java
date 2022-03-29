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
    @Column(name="USER_SUID", nullable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Id
    @Column(name = "WORLD_ID", nullable = false,  columnDefinition = "BIGINT")
    private Long worldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_SUID", referencedColumnName = "SUID", insertable = false, updatable = false)
    private UserEntity userEntity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORLD_ID", referencedColumnName = "WORLD_ID", insertable = false, updatable = false)
    private WorldEntity worldEntity;

    //월드에서 자신의 코드 ( 초대할 때의 코드 )
    @Column(name = "WORLD_USER_CODE", nullable = false, updatable = false, columnDefinition = "CHAR(6)")
    private String worldUserCode;

    //월드에 초대 받은 코드 ( 초대자의 코드 )
    @Column(name = "WORLD_INVITATION_CODE", nullable = false, updatable = false, columnDefinition = "CHAR(6)")
    private String worldinvitationCode;

}
