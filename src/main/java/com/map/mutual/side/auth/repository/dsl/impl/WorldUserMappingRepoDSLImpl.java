package com.map.mutual.side.auth.repository.dsl.impl;

import com.map.mutual.side.auth.model.dto.QUserInWorld;
import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.repository.dsl.WorldUserMappingRepoDSL;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.world.model.dto.QWorldDto;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.model.entity.QWorldEntity;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javafx.beans.binding.LongExpression;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorldUserMappingRepoDSLImpl implements WorldUserMappingRepoDSL {

    private final JPAQueryFactory jpaQueryFactory;

    public WorldUserMappingRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    //참여 중인 월드 리스트 조회 ( 상세정보 조회 )
    // 상세정보 : 자신의 월드인지, 참여자 수 등.
    @Override
    public List<WorldDto> findBySuidWithWorldDetails(String suid) {

        // 1. 참여중인 월드만 조회가 되어야함.
        // 2. 월드별로 참여 중인 사용자 카운트를 알아야함.
        // 3. 한번에 조회가 되어야함.
        //todo group by 성능 향상 필요함.
        List<WorldDto> world = jpaQueryFactory.select(new QWorldDto(QWorldEntity.worldEntity.worldId,
                                                    QWorldEntity.worldEntity.worldName,
                                                    QWorldEntity.worldEntity.worldDesc,
                        new CaseBuilder().when(QWorldEntity.worldEntity.worldOwner.eq(suid)).then("Y").otherwise("N"),
                                                    QWorldEntity.worldEntity.count()))
                .from(QWorldEntity.worldEntity)
                .innerJoin(QWorldUserMappingEntity.worldUserMappingEntity)
                .on(QWorldEntity.worldEntity.worldId.eq(QWorldUserMappingEntity.worldUserMappingEntity.worldId))
                .where(QWorldUserMappingEntity.worldUserMappingEntity.worldId.in(
                        JPAExpressions.select(QWorldUserMappingEntity.worldUserMappingEntity.worldId)
                                .from(QWorldUserMappingEntity.worldUserMappingEntity)
                                .where(QWorldUserMappingEntity.worldUserMappingEntity.userSuid.eq(suid))
                ))
                .groupBy(QWorldEntity.worldEntity.worldId,
                        QWorldEntity.worldEntity.worldName,
                        QWorldEntity.worldEntity.worldDesc,
                        QWorldEntity.worldEntity.worldOwner)
                .fetch();


        return world;
    }

    // 참여 중인 월드 리스트 (심플 조회)
    @Override
    public List<WorldDto> findBySuidWithWorld(String suid) {

        // 1. 참여중인 월드만 조회가 되어야함.
        // 2. 월드별로 참여 중인 사용자 카운트를 알아야함.
        // 3. 한번에 조회가 되어야함.

        List<WorldDto> world = jpaQueryFactory.select(new QWorldDto(QWorldEntity.worldEntity.worldId,
                        QWorldEntity.worldEntity.worldName))
                .from(QWorldEntity.worldEntity)
                .innerJoin(QWorldUserMappingEntity.worldUserMappingEntity)
                .on(QWorldEntity.worldEntity.worldId.eq(QWorldUserMappingEntity.worldUserMappingEntity.worldId))
                .where(QWorldUserMappingEntity.worldUserMappingEntity.userSuid.eq(suid))
                .fetch();

        return world;
    }

    //월드에 참여중인 월드 리스트 조회
    @Override
    public List<UserInWorld> findAllUsersInWorld(long worldId) {

        QUserEntity userA = new QUserEntity("userA");
        QUserEntity userB = new QUserEntity("userB");
        QWorldUserMappingEntity mapA = new QWorldUserMappingEntity("mapA");
        QWorldUserMappingEntity mapB = new QWorldUserMappingEntity("mapB");

        List<UserInWorld> UserInfoInWorld = jpaQueryFactory
                .select(new QUserInWorld(userA.suid,
                        userA.userId,
                        userA.name,
                        userA.profileUrl,
                        userB.userId))
                .from(userA)
                .innerJoin(mapA)
                    .on(mapA.worldId.eq(worldId).and( userA.suid.eq(mapA.userSuid) )) //해당 월드의
                .leftJoin(mapB)
                    .on(mapA.worldinvitationCode.eq(mapB.worldUserCode))
                .innerJoin(userB)
                    .on(mapB.userEntity.suid.eq(userB.suid))
                .fetchJoin()
                .fetch();


        return UserInfoInWorld;
    }

    // 월드 초대 코드로 월드에 입장하려는 사용자 SUID가 월드에 이미 존재하는지 체크하는 쿼리.
    // 존재하면 null 존재하지않으면 [입장 worldId]
    @Override
    public Long exsistUserCodeInWorld (String worldinvitationCode, String suid)
    {

        Long worldId = jpaQueryFactory.select(QWorldUserMappingEntity.worldUserMappingEntity.worldId)
                .from(QWorldUserMappingEntity.worldUserMappingEntity) // 월드초대 코드를 지닌 사용자의 월드정보를 알아낸다.
                .where(QWorldUserMappingEntity.worldUserMappingEntity.worldUserCode.eq(worldinvitationCode))
                .fetchOne();

        //월드코드를 가진 사용자의 월드 ID가 없는경우.
        if(worldId == null){
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR, "월드 코드를 가진 사용자 월드가 없습니다.");
        }


        Integer result = jpaQueryFactory.selectOne()
                .from(QWorldUserMappingEntity.worldUserMappingEntity)
                .where(QWorldUserMappingEntity.worldUserMappingEntity.userSuid.eq(suid)
                        .and(QWorldUserMappingEntity.worldUserMappingEntity.worldId.eq(worldId)))
                .fetchOne();

        //월드에 참여중 아님.
        if(result == null)
            return worldId;
        else // 월드에 참여중.
            return null;

    }


    @Override
    public Boolean exsistUserInWorld (Long worldId, String suid)
    {

        Integer result = jpaQueryFactory.selectOne()
                .from(QWorldUserMappingEntity.worldUserMappingEntity)
                .where(QWorldUserMappingEntity.worldUserMappingEntity.userSuid.eq(suid)
                        .and(QWorldUserMappingEntity.worldUserMappingEntity.worldId.eq(worldId)))
                .fetchOne();

        //월드에 존재 안함.
        if(result == null)
            return false;
        else // 월드에 참여중.
            return true;

    }
}
