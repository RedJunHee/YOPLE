package com.map.mutual.side.world.model.dto;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class       : WorldDetailResponseDto
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-18] - 조 준희 - Class Create
 */
@Getter
@NoArgsConstructor
public class WorldDetailResponseDto {

    private Long worldId;
    private String worldName;
    private String worldDesc;
    private UserInfoDto hostUser;
    private String isMyworld;
    private Long worldUserCnt;

    @Builder
    public WorldDetailResponseDto(Long worldId, String worldName, String worldDesc, UserInfoDto hostUser, String isMyworld, Long worldUserCnt) {
        this.worldId = worldId;
        this.worldName = worldName;
        this.worldDesc = worldDesc;
        this.hostUser = hostUser;
        this.isMyworld = isMyworld;
        this.worldUserCnt = worldUserCnt;
    }
}
