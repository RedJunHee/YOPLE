package com.map.mutual.side.world.repository.dsl.impl;

import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.enumerate.BooleanType;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.world.model.dto.QWorldDetailResponseDto;
import com.map.mutual.side.world.model.dto.WorldDetailResponseDto;
import com.map.mutual.side.world.model.entity.QWorldEntity;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
import com.map.mutual.side.world.repository.dsl.WorldRepoDSL;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Class       : WorldRepoDSLimpl
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-21] - 조 준희 - Class Create
 */
@Repository
public class WorldRepoDSLImpl implements WorldRepoDSL {
    private final static Logger logger = LogManager.getLogger(WorldRepoDSLImpl.class);
    private final JPAQueryFactory jpaQueryFactory;
    @Autowired
    public WorldRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    /**
     * Description : 월드 상세정보 조회
     * - 존재하지 않는 월 드 조회 시 권한 없음 403 에러
     * Name        : getWorldDetail
     * Author      : 조 준 희
     * History     : [2022/04/12] - 조 준 희 - Create
     */
    @Override
    public WorldDetailResponseDto getWorldDetail(Long worldId, String suid) throws YOPLEServiceException {

        WorldDetailResponseDto worldDetailResponseDto = jpaQueryFactory.select(
                        new QWorldDetailResponseDto(QWorldEntity.worldEntity.worldId,
                                QWorldEntity.worldEntity.worldName,
                                QWorldEntity.worldEntity.worldDesc,
                                QUserEntity.userEntity.userId,      //host유저 아이디
                                QUserEntity.userEntity.profileUrl,  //host유저 프로필 사진 경로
                                QWorldUserMappingEntity.worldUserMappingEntity.worldUserCode,
                                new CaseBuilder().when(QWorldEntity.worldEntity.worldOwner.eq(suid)).then(BooleanType.Y.toString()).otherwise(BooleanType.N.toString())))
                .from(QWorldEntity.worldEntity)
                .leftJoin( QUserEntity.userEntity)
                .on(QWorldEntity.worldEntity.worldOwner.eq(QUserEntity.userEntity.suid))
                .fetchJoin()
                .leftJoin(QWorldUserMappingEntity.worldUserMappingEntity)
                .on(QWorldUserMappingEntity.worldUserMappingEntity.userSuid.eq(suid).and(
                        QWorldEntity.worldEntity.worldId.eq(QWorldUserMappingEntity.worldUserMappingEntity.worldId)))
                .fetchJoin()
                .where(QWorldEntity.worldEntity.worldId.eq(worldId))
                .fetchOne();


        // 없는 월드인 경우 권한없음
        if(worldDetailResponseDto == null) {
            logger.error("존재하지 않는 월드의 상세정보 조회 시도.");
            throw new YOPLEServiceException(ApiStatusCode.FORBIDDEN);
        }
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
