package com.map.mutual.side.world.model.dto;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryProjection;
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
    private UserInfoDto host;
    private String worldUserCode;
    private String isMyworld;
    private Long worldUserCnt;

    public void setIsMyworld(String isMyworld) {
        this.isMyworld = isMyworld;
    }

    public void setWorldUserCnt(Long worldUserCnt) {
        this.worldUserCnt = worldUserCnt;
    }

    @Builder
    @QueryProjection()
    public WorldDetailResponseDto(Long worldId, String worldName, String worldDesc, String userId, String profileUrl,String worldUserCode, String isMyworld) {
        this.worldId = worldId;
        this.worldName = worldName;
        this.worldDesc = worldDesc;
        this.host = UserInfoDto.builder().userId(userId).profileUrl(profileUrl).build();
        this.worldUserCode = worldUserCode;
        this.isMyworld =isMyworld;
    }
}
