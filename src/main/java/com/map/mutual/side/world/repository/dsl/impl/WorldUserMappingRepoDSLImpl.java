package com.map.mutual.side.world.repository.dsl.impl;

import com.map.mutual.side.auth.model.dto.QUserInWorld;
import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.dto.notification.WorldEntryNotiDto;
import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.common.utils.CryptUtils;
import com.map.mutual.side.common.utils.YOPLEUtils;
import com.map.mutual.side.review.model.entity.QReviewEntity;
import com.map.mutual.side.review.model.entity.QReviewWorldMappingEntity;
import com.map.mutual.side.world.model.entity.QWorldJoinLogEntity;
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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.lambda.Seq.seq;
import static org.jooq.lambda.tuple.Tuple.tuple;

@Repository
public class WorldUserMappingRepoDSLImpl implements WorldUserMappingRepoDSL {

    private Logger logger = LogManager.getLogger(WorldUserMappingRepoDSLImpl.class);

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public WorldUserMappingRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * Description : 참여 중인 월드 리스트 조회 ( 상세정보 조회 )
     * - 상세정보 : 자신의 월드인지, 참여자 수 등.
     * Name        : findBySuidWithWorldDetails
     * Author      : 조 준 희
     * History     : [2022-04-14] - 조 준 희 - Create
     */
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
                        userB.profileUrl,
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


                                                        if(user.getSuid().equals(suid))  // "나" 자기 자신인 경우. 최상단.
                                                            reviewCount = 9999l;
                                                        else if(user.getIsHost().equals("Y")) // 월드 host 인 경우 2번째 우선순위
                                                            reviewCount= 9998l;
                                                        else if(review == null)
                                                            reviewCount = 0l;
                                                        else
                                                            reviewCount = review.get(1,Long.class);

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
    public Long exsistUserCodeInWorld (String worldinvitationCode, String suid) throws YOPLEServiceException {

        Long worldId = jpaQueryFactory.select(QWorldUserMappingEntity.worldUserMappingEntity.worldId)
                .from(QWorldUserMappingEntity.worldUserMappingEntity) // 월드초대 코드를 지닌 사용자의 월드정보를 알아낸다.
                .where(QWorldUserMappingEntity.worldUserMappingEntity.worldUserCode.eq(worldinvitationCode))
                .fetchOne();

        //월드코드를 가진 사용자의 월드 ID가 없는경우.
        if(worldId == null){
            logger.error("월드에 입장하기 : 월드 초대코드({})를 가진 사용자가 없음.",worldinvitationCode);
            throw new YOPLEServiceException(ApiStatusCode.WORLD_USER_CDOE_VALID_FAILED);
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

    /**
     * Description : 월드 입장 알림 최신건 있는지 여부.
     * Name        : existsNewNoti
     * Author      : 조 준 희
     * History     : [2022/05/30] - 조 준 희 - Create
     */
    @Override
    public boolean existsNewNoti(String suid, LocalDateTime searchLocalDateTime) {

        QWorldUserMappingEntity map = new QWorldUserMappingEntity("map");
        QWorldJoinLogEntity joinLog = new QWorldJoinLogEntity("joinLog");


        boolean existsYN = false;
        existsYN = jpaQueryFactory.select(joinLog.worldId)
                                    .from(map)
                                    .innerJoin(joinLog)
                                    .on(map.worldId.eq(joinLog.worldId))
                                    .where(map.userSuid.eq(suid)
                                            .and(joinLog.createTime.after(searchLocalDateTime)))
                                    .fetchFirst() != null;


        return existsYN;
    }

    /**
     * Description : 월드에 입장하였습니다 알림 조회
     *
     * -- 사용자가 입장되어있는 월드
     * CREATE TABLE #MY_WORLD(
     * WORLD_ID BIGINT
     * ,ENTRY_DATE DATETIME
     * )
     *
     * -- 사용자가 입장되어있는 월드 저장
     * INSERT INTO #MY_WORLD
     * SELECT WORLD_ID, CREATE_DT
     *   FROM WORLD_USER_MAPPING
     *  WHERE USER_SUID = 'YO2022032927090787'
     *
     * SELECT u.[USER_ID],u.PROFILE_URL,w.NAME, other.CREATE_DT
     *   FROM #MY_WORLD as my
     *  INNER JOIN WORLD_USER_MAPPING as other
     *     ON other.CREATE_DT > my.ENTRY_DATE AND my.WORLD_ID = other.WORLD_ID
     *   LEFT JOIN USER_INFO as u
     *     ON other.USER_SUID = u.SUID
     *   LEFT JOIN WORLD w
     *     ON my.WORLD_ID = w.WORLD_ID
     *  ORDER BY other.CREATE_DT DESC
     *
     *   DROP TABLE #MY_WORLD
     * Name        : WorldEntryNotiList
     * Author      : 조 준 희
     * History     : [2022-04-14] - 조 준 희 - Create
     */
    @Override
    public List<WorldEntryNotiDto> WorldEntryNotiList(String suid) {


        List<WorldEntryNotiDto> notis = new ArrayList<>();


        // 내가 참여 중인 월드리스트.
        String sql = "CREATE TABLE #MY_WORLD ( \n" +
                "WORLD_ID BIGINT , \n" +
                "ENTRY_DATE DATETIME \n" +
                ") \n" +

                // 1. 사용자가 참여하고 있는 월드 + 최근 입장한 시간을 가져온다.
                "INSERT INTO #MY_WORLD \n"+
                "SELECT B.WORLD_ID, CREATE_DT\n" +
                "  FROM WORLD_USER_MAPPING A\n" +
                " INNER JOIN (\n" +
                "    SELECT WORLD_ID ,  MAX(CREATE_DT) AS 'CREATE_DT'\n" +
                "      FROM WORLD_JOIN_LOG\n" +
                "     WHERE USER_SUID = ? \n" +
                "     GROUP BY WORLD_ID\n" +
                " ) B\n" +
                "    ON A.WORLD_ID = B.WORLD_ID\n" +
                " WHERE A.USER_SUID = ? \n" +


                "CREATE TABLE #BLOCK(\n" +
                "    BLOCK_SUID VARCHAR(18),\n" +
                        "    START_DT DATETIME,\n" +
                        "    END_DT DATETIME\n" +
                        ")\n" +

                // 2. 차단 리스트에서 사용자가 차단한 목록을 전부 가져온다. 과거 데이터 포함.
                "INSERT INTO #BLOCK\n" +
                "SELECT BLOCK_SUID, CREATE_DT, CASE WHEN IS_BLOCKING = 'Y' THEN '2999-01-01' ELSE UPDATE_DT END\n" +
                "  FROM USER_BLOCK_LOG \n" +
                " WHERE USER_SUID = ? \n" +


                // 3. 내가 입장 한 월드 이후에 입장한 사용자들 입장로그에 유저데이터 + 월드데이터 추가
                // +++ 차단 리스트에 해당하는 유저 입장 로그 중  차단 기간내에 속한 것은 필터링 한다.
                "SELECT u.[USER_ID], u.PROFILE_URL, w.NAME, other.CREATE_DT \n" +
                "  FROM #MY_WORLD as my \n" +
                " INNER JOIN WORLD_JOIN_LOG as other \n" +
                "    ON other.CREATE_DT > my.ENTRY_DATE " +
                "   AND my.WORLD_ID = other.WORLD_ID \n" +
                " INNER JOIN USER_INFO as u \n" +
                "    ON other.USER_SUID = u.SUID \n" +
                " INNER JOIN WORLD w \n" +
                "    ON my.WORLD_ID = w.WORLD_ID \n" +
                "  LEFT JOIN #BLOCK b\n" +   // 내가 차단한 유저 로그.
                "    ON  other.USER_SUID = b.BLOCK_SUID AND  other.CREATE_DT BETWEEN b.START_DT AND b.END_DT\n" +
                " WHERE b.BLOCK_SUID IS NULL \n" +


                // 4. 임시 테이블 삭제.
                "  DROP TABLE #MY_WORLD \n " +
                " DROP TABLE #BLOCK ";

        Query nativeQuery  = entityManager.createNativeQuery(sql).setParameter(1,suid)
                .setParameter(2,suid)
                .setParameter(3,suid);

        List<Object[]> result =  nativeQuery.getResultList();

        for(Object[] obj : result){
            Timestamp notidate = new Timestamp(0);
            if( obj[3] instanceof Timestamp )
                notidate= (Timestamp)obj[3];

            notis.add(WorldEntryNotiDto.builder().userId( obj[0].toString())
                    .userProfileUrl( (obj[1] == null ) ? null :obj[1].toString()) // 프로필 사진은 Optional Column
                    .worldName(obj[2].toString())
                    .notiDate( notidate.toLocalDateTime() )
                    .build());
        }

        return notis;
    }
}
