package com.map.mutual.side.world.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * Class       : WorldAuthResponseDto
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-18] - 조 준희 - Class Create
 */
@Getter
@NoArgsConstructor
public class WorldAuthResponseDto {
    private String worldName;
    private String worldUserCode;


    @Builder
    public WorldAuthResponseDto(String worldName, String worldUserCode) {
        this.worldName = worldName;
        this.worldUserCode = worldUserCode;
    }
}
