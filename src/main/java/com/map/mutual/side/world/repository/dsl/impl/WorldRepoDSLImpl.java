package com.map.mutual.side.world.repository.dsl.impl;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.world.model.dto.WorldDetailResponseDto;
import com.map.mutual.side.world.model.entity.QWorldEntity;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
import com.map.mutual.side.world.model.entity.WorldEntity;
import com.map.mutual.side.world.repository.dsl.WorldRepoDSL;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

        WorldEntity world = jpaQueryFactory.select(QWorldEntity.worldEntity)
                .from(QWorldEntity.worldEntity)
//                .leftJoin(QWorldEntity.worldEntity.host, QUserInfoEntity.userInfoEntity)
                .fetchJoin()
                .where(QWorldEntity.worldEntity.worldId.eq(worldId))
                .fetchOne();

        WorldDetailResponseDto worldDetailResponseDto
                = WorldDetailResponseDto.builder().worldId(world.getWorldId())
                .worldName(world.getWorldName())
                .worldDesc(world.getWorldDesc())
//                .isMyworld( (world.getHost().getSuid().equals(requestUser.getSuid()))? BooleanType.Y.toString(): BooleanType.N.toString())
                //호스트 유저 정보
//                .hostUser(UserInfoDto.builder().userId(world.getHost().getUserId())
//                        .profileUrl(world.getHost().getProfileUrl()).build())
                //todo 다시 Select 나가고 있음.
                .worldUserCnt(worldUserCnt(worldId))
                .build();

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
