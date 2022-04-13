package com.map.mutual.side.auth.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class UserWorldInvitionDto {
    @NotBlank
    private String targetSuid;
    @NotNull
    private Long worldId;

    @Builder
    public UserWorldInvitionDto(String targetSuid, Long worldId) {
        this.targetSuid = targetSuid;
        this.worldId = worldId;
    }
}
