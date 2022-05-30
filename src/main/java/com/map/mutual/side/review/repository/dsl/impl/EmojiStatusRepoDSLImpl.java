package com.map.mutual.side.review.repository.dsl.impl;

import com.fasterxml.jackson.core.io.BigDecimalParser;
import com.map.mutual.side.auth.model.dto.notification.EmojiNotiDto;
import com.map.mutual.side.review.model.entity.QEmojiStatusNotiEntity;
import com.map.mutual.side.review.repository.dsl.EmojiStatusRepoDSL;
import com.map.mutual.side.world.model.entity.QWorldJoinLogEntity;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * fileName       : PlaceRepoDSLImpl
 * author         : kimjaejung
 * createDate     : 2022/04/05
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/04/05        kimjaejung       최초 생성
 *
 */
@Repository
public class EmojiStatusRepoDSLImpl implements EmojiStatusRepoDSL {
    private final JPAQueryFactory jpaQueryFactory;
    @Autowired
    public EmojiStatusRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Description : 이모지 알림 최신건 있는지 여부.
     * Name        : existsNewNoti
     * Author      : 조 준 희
     * History     : [2022/05/30] - 조 준 희 - Create
     */
    @Override
    public boolean existsNewNoti(String suid, LocalDateTime searchLocalDateTime) {

        QEmojiStatusNotiEntity notis = new QEmojiStatusNotiEntity("notis");

        boolean existsYN = false;
        existsYN = jpaQueryFactory.select(notis.userSuid)
                .from(notis)
                .where(notis.userSuid.eq(suid).and(notis.createTime.after(searchLocalDateTime)))
                .fetchFirst() != null;

        return existsYN;
    }

    /**
     * Description :
     CREATE TABLE #EMOJI_NOTIS(
     WORLD_ID BIGINT,
     REVIEW_ID BIGINT,
     USER_SUID VARCHAR(18),
     CREATE_DT DATETIME
     )

     CREATE TABLE #BLOCK(
     BLOCK_SUID VARCHAR(18),
     START_DT DATETIME,
     END_DT DATETIME
     )

     INSERT INTO #BLOCK
     SELECT BLOCK_SUID, CREATE_DT, CASE WHEN IS_BLOCKING = 'Y' THEN '2999-01-01' ELSE UPDATE_DT END
     FROM USER_BLOCK_LOG
     WHERE USER_SUID = 'YO2022042527090787'

     -- 이모지 이력에  차단 이력 넣는다
     -- 각 이모지 이력은 차단상태에서 쌓인건지 나온다.
     -- 안 쌓인 것들  뽑아서 그룹핑으로 1건 처리한다.
     INSERT INTO #EMOJI_NOTIS
     SELECT e.WORLD_ID, e.REVIEW_ID , e.USER_SUID, e.CREATE_DT, b.*
     FROM  EMOJI_STATUS_NOTI e
     LEFT JOIN REVIEW r
     ON r.REVIEW_ID = e.REVIEW_ID
     LEFT JOIN #BLOCK b
     ON  e.USER_SUID = b.BLOCK_SUID AND b.START_DT <= e.CREATE_DT  AND e.CREATE_DT <= b.END_DT
     WHERE e.CREATE_DT BETWEEN  '1999-01-01' AND '2022-05-06' AND  r.USER_SUID = 'YO2022042527090787' AND BLOCK_SUID IS NULL

     SELECT notis.REVIEW_ID, notis.WORLD_ID,
     u.[USER_ID],u.PROFILE_URL, w.NAME, place.PLACE_ID, place.X, place.Y, notis.CREATE_DT
     FROM #EMOJI_NOTIS notis
     LEFT JOIN REVIEW r
     ON notis.REVIEW_ID = r.REVIEW_ID
     LEFT JOIN PLACE place
     ON place.PLACE_ID = r.PLACE_ID
     LEFT JOIN USER_INFO u
     ON notis.USER_SUID = u.SUID
     LEFT JOIN WORLD w
     ON notis.WORLD_ID = w.WORLD_ID
     ORDER BY notis.CREATE_DT DESC
     * Name        :
     * Author      : 조 준 희
     * History     : [2022/04/27] - 조 준 희 - Create
     */
    @Override
    public List<EmojiNotiDto> findEmojiNotis(String suid) {


        List<EmojiNotiDto> notis = new ArrayList<>();

        String sql =
                // 이모지 이력 전체.
                "CREATE TABLE #EMOJI_NOTIS(\n" +
                "    WORLD_ID BIGINT,\n" +
                "    REVIEW_ID BIGINT,\n" +
                "    USER_SUID VARCHAR(18),\n" +
                "    CREATE_DT DATETIME \n" +
                ")\n" +

                // 사용자 차단 리스트 목록
                "CREATE TABLE #BLOCK(\n" +
                "    BLOCK_SUID VARCHAR(18),\n" +
                "    START_DT DATETIME,\n" +
                "    END_DT DATETIME\n" +
                ")\n" +

                "INSERT INTO #BLOCK\n" +
                "SELECT BLOCK_SUID, CREATE_DT, CASE WHEN IS_BLOCKING = 'Y' THEN '2999-01-01' ELSE UPDATE_DT END\n" +
                "  FROM USER_BLOCK_LOG \n" +
                " WHERE USER_SUID = ? \n" +

                // 자기 자신의 이모지 알림은 차단하기 위해 차단리스트에 사용자 추가.
                "INSERT INTO #BLOCK \n" +
                 "VALUES (? ,'2000-01-01','2999-01-01') \n" +

                 // 이모지 이력의 최초 1회만 해당하는 알림으로 MIN(이모지 시간)으로 차단 아닌 상태에서 달린 이모지 처음꺼 시간 가져옴.
                "INSERT INTO #EMOJI_NOTIS\n" +
                "SELECT e.WORLD_ID, e.REVIEW_ID , e.USER_SUID, e.CREATE_DT \n" +
                "  FROM  EMOJI_STATUS_NOTI e\n" +
                " INNER JOIN REVIEW r\n" +
                "    ON r.REVIEW_ID = e.REVIEW_ID\n" +
                "  LEFT JOIN #BLOCK b\n" +
                "    ON  e.USER_SUID = b.BLOCK_SUID AND b.START_DT <= e.CREATE_DT  AND e.CREATE_DT <= b.END_DT\n" +
                " WHERE e.CREATE_DT BETWEEN  ? AND ? AND  r.USER_SUID = ? AND BLOCK_SUID IS NULL\n" +

                "  SELECT notis.REVIEW_ID, notis.WORLD_ID, \n" +
                "        u.[USER_ID], " +
                 "      u.PROFILE_URL, " +
                        "w.NAME, " +
                        "place.PLACE_ID, " +
                        "place.X, " +
                        "place.Y, " +
                        "notis.CREATE_DT\n" +
                "   FROM #EMOJI_NOTIS notis\n" +
                "  INNER JOIN REVIEW r\n" +
                "     ON notis.REVIEW_ID = r.REVIEW_ID\n" +
                "  INNER JOIN PLACE place\n" +
                "     ON place.PLACE_ID = r.PLACE_ID\n" +
                "  INNER JOIN USER_INFO u\n" +
                "     ON notis.USER_SUID = u.SUID\n" +
                "  INNER JOIN WORLD w\n" +
                "     ON notis.WORLD_ID = w.WORLD_ID " +
                "  ORDER BY notis.CREATE_DT DESC \n" +

                "DROP TABLE #BLOCK \n" +
                "DROP TABLE #EMOJI_NOTIS";

         Query nativeQuery = entityManager.createNativeQuery(sql)
                                .setParameter(1, suid)
                                .setParameter(2, suid)
                                .setParameter(3,LocalDateTime.now().minusWeeks(12))
                                .setParameter(4, LocalDateTime.now())
                                .setParameter(5, suid);

        List<Object[]> result =  nativeQuery.getResultList();

        for(Object[] obj : result){

            Timestamp notidate = new Timestamp(0);
            if( obj[8] instanceof Timestamp )
                notidate= (Timestamp)obj[8];

            notis.add(EmojiNotiDto.builder().reviewId(Long.parseLong( obj[0].toString()))
                            .worldId(Long.parseLong(obj[1].toString()))
                            .userId(obj[2].toString())
                            .userProfileUrl( (obj[3] == null ) ? null : obj[3].toString()) // 프로필 사진은 Optional Column
                            .worldName(obj[4].toString())
                            .placeId(obj[5].toString())
                            .x(BigDecimalParser.parse(obj[6].toString()))
                            .y(BigDecimalParser.parse(obj[7].toString()))
                            .notiDate( notidate.toLocalDateTime() )
                    .build());
        }
        return notis;
    }
}
