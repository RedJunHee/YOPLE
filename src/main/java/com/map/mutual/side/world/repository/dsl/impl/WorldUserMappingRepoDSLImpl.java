package com.map.mutual.side.world.repository.dsl.impl;

import com.map.mutual.side.auth.model.dto.QUserInWorld;
import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.review.model.entity.QReviewEntity;
import com.map.mutual.side.review.model.entity.QReviewWorldMappingEntity;
import com.map.mutual.side.world.repository.dsl.WorldUserMappingRepoDSL;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.world.model.dto.QWorldDto;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.model.entity.QWorldEntity;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.lambda.Seq.seq;
import static org.jooq.lambda.tuple.Tuple.tuple;

@Repository
public class WorldUserMappingRepoDSLImpl implements WorldUserMappingRepoDSL {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
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
        List<WorldDto> world = new ArrayList<>();

        String sql = "CREATE TABLE #WORLD_COUNT ( " +
                "WORLD_ID BIGINT , " +
                "USER_COUNT INT " +
                ") \n" +

                "INSERT INTO #WORLD_COUNT "+
                "SELECT A.WORLD_ID, COUNT(B.WORLD_ID)" +
                "  FROM WORLD_USER_MAPPING as A " + // --참여 중 월드
                " INNER JOIN WORLD_USER_MAPPING as B " + //-- 전체 월드
                "    ON A.USER_SUID = ? AND A.WORLD_ID = B.WORLD_ID " +
                " GROUP BY A.WORLD_ID    \n" +

                "SELECT A.WORLD_ID as 'WORLD_ID' " +
                "        , W.[NAME] as 'WORLD_NAME' " +
                "        , W.[DESCRIPTION] as 'WORLD_DESC' " +
                "        , CASE WHEN W.WORLD_OWNER = ? THEN 'Y' ELSE 'N' END as 'isMyWorld' " +
                "        , A.USER_COUNT as 'WORLD_USER_COUNT' " +
                "        , M.WORLD_USER_CODE as WORLD_USER_CODE " +
                "  FROM #WORLD_COUNT as A " +
                "  LEFT JOIN WORLD as W " +
                "    ON A.WORLD_ID = W.WORLD_ID " +
                "  LEFT JOIN WORLD_USER_MAPPING as M" +
                "    ON A.WORLD_ID = M.WORLD_ID AND M.USER_SUID = ? " +
                "ORDER BY CASE WHEN  W.WORLD_OWNER = ? THEN 1 ELSE 0 END DESC " +
                "          ,ACCESS_TIME DESC \n" +

                " DROP TABLE #WORLD_COUNT ";

        Query nativeQuery  = entityManager.createNativeQuery(sql).setParameter(1,suid)
                .setParameter(2,suid)
                .setParameter(3,suid)
                .setParameter(4,suid);

        List<Object[]> result =  nativeQuery.getResultList();

        for(Object[] obj : result){
            world.add(WorldDto.builder().worldId( Long.parseLong(obj[0].toString()))
                    .worldName(obj[1].toString())
                    .worldDesc(obj[2].toString())
                    .isMyworld(obj[3].toString())
                    .worldUserCnt(Long.parseLong(obj[4].toString()))
                    .worldUserCode(obj[5].toString())
                    .build());
        }

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
                .orderBy( new CaseBuilder().when(QWorldEntity.worldEntity.worldOwner.eq(suid)).then(1).otherwise(0).desc()
                        , QWorldUserMappingEntity.worldUserMappingEntity.accessTime.desc())
                .fetch();

        return world;
    }

    /**
     * Name        : findAllUsersInWorld
     * Author      : 조 준 희
     * Description : 월드에 참여 중인 사용자 조회하기.
     * History     : [2022/04/10] - 조 준 희 - Create
     */
    @Override
    public List<UserInWorld> findAllUsersInWorld(long worldId, String suid) {

        QUserEntity userA = new QUserEntity("userA");
        QUserEntity userB = new QUserEntity("userB");
        QWorldUserMappingEntity mapA = new QWorldUserMappingEntity("mapA");
        QWorldUserMappingEntity mapB = new QWorldUserMappingEntity("mapB");


        // 1. 해당 월드 사용자들의 리뷰 작성 수 조회
        List<Tuple> reviewCnt = jpaQueryFactory
                .select(QReviewEntity.reviewEntity.userEntity.suid,
                        QReviewWorldMappingEntity.reviewWorldMappingEntity.reviewEntity.reviewId.count())
                .from(QReviewEntity.reviewEntity)
                .innerJoin(QReviewWorldMappingEntity.reviewWorldMappingEntity)
                .on(QReviewEntity.reviewEntity.reviewId.eq(QReviewWorldMappingEntity.reviewWorldMappingEntity.reviewEntity.reviewId))
                .where(QReviewWorldMappingEntity.reviewWorldMappingEntity.worldEntity.worldId.eq(worldId))
                .groupBy(QReviewEntity.reviewEntity.userEntity.suid)
                .orderBy(QReviewWorldMappingEntity.reviewWorldMappingEntity.reviewEntity.reviewId.count().desc())
                .fetch();

        // 2. 월드 참여자들 조회. (초대자 포함.)
        List<UserInWorld> userInfoInWorld = jpaQueryFactory
                .select(new QUserInWorld(userA.suid,
                        userA.userId,
                        userA.name,
                        userA.profileUrl,
                        userB.userId,
                        // 월드에 참여 중인 사용자SUID와 초대자SUID가 같다면 Host사용자
                        new CaseBuilder().when(mapA.userSuid.eq(mapB.userSuid)).then("Y").otherwise("N")))
                .from(userA)
                .innerJoin(mapA) // 1. 월드에 참여 중인 사용자 필터링.
                    .on(mapA.worldId.eq(worldId).and( userA.suid.eq(mapA.userSuid) ))
                .leftJoin(mapB) // 2.  월드에 참여 중인 사용자들 초대자 판별.
                    .on(mapA.worldinvitationCode.eq(mapB.worldUserCode))
                .innerJoin(userB)   // 3. 초대자 정보 조회 조인
                    .on(mapB.userEntity.suid.eq(userB.suid))
                .fetchJoin()
                .fetch();


        // 3. Left Outer Join && 자기자신, 호스트 사용자 최상단 정렬 및 리뷰 수 정렬.
        List<UserInWorld> list = seq(userInfoInWorld)
                                    .flatMap( user ->
                                            seq(reviewCnt)
                                                    // 조인 조건.
                                                    .filter( review -> user.getSuid().equals(review.get(0, String.class).toString()))
                                                    .onEmpty(null)  // left outer join
                                                    .map(review -> { // select
                                                        long reviewCount = 0l;

                                                        if(review == null)
                                                            reviewCount = 0l;
                                                        else if(user.getSuid().equals(suid))  // "나" 자기 자신인 경우. 최상단.
                                                            reviewCount = 9999l;
                                                        else if(user.getIsHost().equals("Y")) // 월드 host 인 경우 2번째 우선순위
                                                            reviewCount= 9998l;

                                                        return tuple(user, reviewCount);
                                                    })
                                    )
                                .sorted( Comparator.comparingLong( v -> Long.parseLong(v.v2.toString()))).reverse() // order by
                                .map(v -> v.v1) //select
                                .collect(Collectors.toList());

        return list;
    }

    // 월드 초대 코드로 월드에 입장하려는 사용자 SUID가 월드에 이미 존재하는지 체크하는 쿼리.
    // 존재하면 null 존재하지않으면 [입장 worldId]
    @Override
    public Long exsistUserCodeInWorld (String worldinvitationCode, String suid) throws YOPLEServiceException
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


    //월드에 해당 사용자가 입장되어있는지 확인.
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
