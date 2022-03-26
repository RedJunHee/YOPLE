package com.map.mutual.side.auth.repository.dsl.impl;

import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.repository.dsl.WorldUserMappingRepoDSL;
import com.map.mutual.side.world.model.dto.QWorldDto;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.model.entity.QWorldEntity;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
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

//                                                    new CaseBuilder().when(QWorldEntity.worldEntity.host.suid.eq(suid)).then("Y").otherwise("N"),
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
                        QWorldEntity.worldEntity.worldOwner).fetch();


        return world;
    }

    @Override
    public List<UserEntity> findAllUsersInWorldCode(long worldId) {
        return jpaQueryFactory
                .select(QUserEntity.userEntity)
                .from(QUserEntity.userEntity)
                .innerJoin(QWorldUserMappingEntity.worldUserMappingEntity)
                .where(QWorldUserMappingEntity.worldUserMappingEntity.worldId.eq(worldId))
                .on(QUserEntity.userEntity.suid.eq(QWorldUserMappingEntity.worldUserMappingEntity.userSuid))
                .fetch();
    }
}
