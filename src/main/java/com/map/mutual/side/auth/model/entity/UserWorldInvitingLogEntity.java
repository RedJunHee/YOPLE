package com.map.mutual.side.auth.model.entity;


import com.map.mutual.side.auth.model.keys.UserWorldInvitingLogKeys;
import com.map.mutual.side.common.repository.config.CreateDtEntity;
import com.map.mutual.side.common.repository.config.TimeEntity;
import lombok.*;

import javax.persistence.*;

@Table(name= "USER_WORLD_INVITING_LOG")
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(UserWorldInvitingLogKeys.class)
public class UserWorldInvitingLogEntity extends TimeEntity {

    @Id
    @Column(name = "WORLD_INVITATION_CODE", nullable = false, updatable = false, columnDefinition = "CHAR(6)")
    private String worldinvitationCode;

    @Id
    @Column(name = "USER_SUID", nullable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Id
    @Column(name = "TARGET_SUID", nullable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String targetSuid;

}
