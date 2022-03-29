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
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorldUserMappingRepoDSLImpl implements WorldUserMappingRepoDSL {

    private final JPAQueryFactory jpaQueryFactory;

    public WorldUserMappingRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    /**
     *  월드 별 참여자 수를 알아야 함.
     *    => 월드 그룹으로 지어 Mapping테이블에서 카운팅 해야함.
     *  그룹을 참여자가 속해 있는 월드로만 그룹 조건인 Having이 들어가야함.
     *

     SELECT *
       FROM WORLD w
      INNER JOIN (
                SELECT wum.ID as ID, COUNT(wum.ID) as cnt
                  FROM WORLD_USER_MAPPING wum
        `        INNER JOIN WORLD_USER_MAPPING wum2
                    ON wum .ID =wum2 .ID and  wum2.SUID = 'YO2022031827090787'
                 GROUP BY wum.ID
                )a
     ON w.ID = a.ID
     * */
    @Override
    public List<WorldDto> findBySuidWithWorld(String suid) {

        // 1. 참여중인 월드만 조회가 되어야함.
        // 2. 월드별로 참여 중인 사용자 카운트를 알아야함.
        // 3. 한번에 조회가 되어야함.
        /**
         SELECT w.ID ,w.WORLD_NAME ,w.WORLD_DESC , COUNT(1)
           FROM WORLD w
          INNER JOIN WORLD_USER_MAPPING wum
             ON w.ID =wum.ID
          WHERE wum.ID IN (
                        SELECT ID
                        FROM WORLD_USER_MAPPING wum2
                        WHERE SUID = 'YO2022031827090787'
                        )
          GROUP BY w.ID ,w.WORLD_NAME ,w.WORLD_DESC
         * */

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
    public Long exsistUserInWorld (String worldinvitationCode, String suid)
    {

        Long worldId = jpaQueryFactory.select(QWorldUserMappingEntity.worldUserMappingEntity.worldId)
                .from(QWorldUserMappingEntity.worldUserMappingEntity) // 월드초대 코드를 지닌 사용자의 월드정보를 알아낸다.
                .where(QWorldUserMappingEntity.worldUserMappingEntity.worldUserCode.eq(worldinvitationCode))
                .fetchOne();

        //월드코드를 가진 사용자의 월드 ID가 없는경우.
        if(worldId == null)
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);


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

}
