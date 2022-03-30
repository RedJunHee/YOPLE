package com.map.mutual.side.auth.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserWorldInvitionDto {
    private String targetSuid;
    private String worldInvitationCode;


    @Builder
    @QueryProjection
    public UserWorldInvitionDto(String targetSuid, String worldInvitationCode) {
        this.targetSuid = targetSuid;
        this.worldInvitationCode = worldInvitationCode;
    }
}
