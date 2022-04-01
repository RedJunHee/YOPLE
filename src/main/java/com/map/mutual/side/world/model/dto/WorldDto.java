package com.map.mutual.side.world.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class       : WorldDto
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-18] - 조 준희 - Class Create
 */
@Getter
@NoArgsConstructor
public class WorldDto {

    private Long worldId;
    private String worldName;
    private String worldDesc;
    private String isMyworld;
    private Long worldUserCnt;
    private String worldUserCode;

    @QueryProjection
    public WorldDto(Long worldId, String worldName) {
        this.worldId = worldId;
        this.worldName = worldName;
    }

    @QueryProjection
    public WorldDto(Long worldId, String worldName, String worldDesc ,String isMyworld, Long worldUserCnt) {
        this.worldId = worldId;
        this.worldName = worldName;
        this.worldDesc = worldDesc;
        this.isMyworld = isMyworld;
        this.worldUserCnt = worldUserCnt;
    }

    @Builder
    public WorldDto(Long worldId, String worldName, String worldDesc, String isMyworld, Long worldUserCnt, String worldUserCode) {
        this.worldId = worldId;
        this.worldName = worldName;
        this.worldDesc = worldDesc;
        this.isMyworld = isMyworld;
        this.worldUserCnt = worldUserCnt;
        this.worldUserCode = worldUserCode;
    }
}
