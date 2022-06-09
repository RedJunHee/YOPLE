package com.map.mutual.side.auth.model.entity;

import com.map.mutual.side.common.repository.config.TimeEntity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Table(name= "USER_WORLD_INVITING_LOG")
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserWorldInvitingLogEntity extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ", nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long seq;

    @Column(name = "WORLD_ID", nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long worldId;

    @Column(name = "USER_SUID", nullable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Column(name = "TARGET_SUID", nullable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String targetSuid;

    @Column(name = "INVITING_STATUS",nullable = false, columnDefinition = "CHAR(1)" )
    @ColumnDefault("'-'")
    private String invitingStatus;

    @Column(name = "WORLD_USER_CODE", nullable = false, columnDefinition = "CHAR(6)")
    private String worldUserCode;

    public void inviteAccept()
    {
        invitingStatus = "Y";
    }

    public void inviteReject()
    {
        invitingStatus = "N";
    }

    @Override
    public String toString() {
        return "UserWorldInvitingLogEntity{" +
                "seq=" + seq +
                ", worldId=" + worldId +
                ", userSuid='" + userSuid + '\'' +
                ", targetSuid='" + targetSuid + '\'' +
                ", invitingStatus='" + invitingStatus + '\'' +
                ", worldUserCode='" + worldUserCode + '\'' +
                '}';
    }
}
