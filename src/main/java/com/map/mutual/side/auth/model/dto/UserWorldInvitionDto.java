package com.map.mutual.side.auth.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserWorldInvitionDto {
    private String targetSuid;
    private Long worldId;

    @Builder
    public UserWorldInvitionDto(String targetSuid, Long worldId) {
        this.targetSuid = targetSuid;
        this.worldId = worldId;
    }
}
