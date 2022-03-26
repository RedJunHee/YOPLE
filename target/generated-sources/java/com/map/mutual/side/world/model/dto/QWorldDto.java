package com.map.mutual.side.world.model.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.Generated;

/**
 * com.map.mutual.side.world.model.dto.QWorldDto is a Querydsl Projection type for WorldDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QWorldDto extends ConstructorExpression<WorldDto> {

    private static final long serialVersionUID = 1862553145L;

    public QWorldDto(com.querydsl.core.types.Expression<Long> worldId, com.querydsl.core.types.Expression<String> worldName, com.querydsl.core.types.Expression<String> worldDesc, com.querydsl.core.types.Expression<String> isMyworld, com.querydsl.core.types.Expression<Long> worldUserCnt) {
        super(WorldDto.class, new Class<?>[]{long.class, String.class, String.class, String.class, long.class}, worldId, worldName, worldDesc, isMyworld, worldUserCnt);
    }

}

