package com.map.mutual.side.world.repository.dsl.impl;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.common.enumerate.BooleanType;
import com.map.mutual.side.world.model.dto.QWorldDetailResponseDto;
import com.map.mutual.side.world.model.dto.WorldDetailResponseDto;
import com.map.mutual.side.world.model.entity.QWorldEntity;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
import com.map.mutual.side.world.model.entity.WorldEntity;
import com.map.mutual.side.world.repository.dsl.WorldRepoDSL;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.criterion.NullExpression;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Repository;

/**
 * Class       : WorldRepoDSLimpl
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-21] - 조 준희 - Class Create
 */
@Repository
public class WorldRepoDSLImpl implements WorldRepoDSL {

    private final JPAQueryFactory jpaQueryFactory;

    public WorldRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public WorldDetailResponseDto getWorldDetail(Long worldId, UserInfoDto requestUser) {


        WorldDetailResponseDto worldDetailResponseDto = jpaQueryFactory.select(
                        new QWorldDetailResponseDto(QWorldEntity.worldEntity.worldId,
                                QWorldEntity.worldEntity.worldName,
                                QWorldEntity.worldEntity.worldDesc,
                                QUserEntity.userEntity.userId,
                                QUserEntity.userEntity.profileUrl,
                                new CaseBuilder().when(QWorldEntity.worldEntity.worldOwner.eq(requestUser.getSuid())).then(BooleanType.Y.toString()).otherwise(BooleanType.N.toString())))
                .from(QWorldEntity.worldEntity)
                .leftJoin( QUserEntity.userEntity)
                .on(QWorldEntity.worldEntity.worldOwner.eq(QUserEntity.userEntity.suid))
                .fetchJoin()
//                .join(QWorldUserMappingEntity.worldUserMappingEntity)
//                .on(QWorldEntity.worldEntity.worldId.eq(QWorldUserMappingEntity.worldUserMappingEntity.worldId))
                .where(QWorldEntity.worldEntity.worldId.eq(worldId))
                .fetchOne();

        worldDetailResponseDto.setWorldUserCnt(worldUserCnt(worldId));


        return worldDetailResponseDto;
    }

    private Long worldUserCnt(Long worldId)
    {
        return jpaQueryFactory.select(QWorldUserMappingEntity.worldUserMappingEntity.count())
                .from(QWorldUserMappingEntity.worldUserMappingEntity)
                .where(QWorldUserMappingEntity.worldUserMappingEntity.worldEntity.worldId.eq(worldId))
                .fetchOne();
    }
}
