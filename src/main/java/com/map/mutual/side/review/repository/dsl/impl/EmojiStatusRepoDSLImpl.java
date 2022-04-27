package com.map.mutual.side.review.repository.dsl.impl;

import com.fasterxml.jackson.core.io.BigDecimalParser;
import com.map.mutual.side.auth.model.dto.notification.EmojiNotiDto;
import com.map.mutual.side.review.repository.dsl.EmojiStatusRepoDSL;
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
     * Description :
     * CREATE TABLE #EMOJI_NOTIS(
     *     WORLD_ID BIGINT,
     *     REVIEW_ID BIGINT,
     *     USER_SUID VARCHAR(18),
     *     CREATE_DT DATETIME
     * )
     *
     * CREATE TABLE #BLOCK(
     *     BLOCK_SUID VARCHAR(18),
     *     START_DT DATETIME,
     *     END_DT DATETIME
     * )
     *
     * INSERT INTO #BLOCK
     * SELECT BLOCK_SUID, CREATE_DT, CASE WHEN IS_BLOCKING = 'Y' THEN '2999-01-01' ELSE UPDATE_DT END
     *     FROM USER_BLOCK_LOG
     *     WHERE USER_SUID = 'YO2022042527090787'
     *
     * 	SELECT * FROM #BLOCK
     *
     *
     * INSERT INTO #EMOJI_NOTIS
     * SELECT e.WORLD_ID, e.REVIEW_ID , e.USER_SUID, e.CREATE_DT, b.BLOCK_SUID
     *     FROM  EMOJI_STATUS e
     *     LEFT JOIN REVIEW r
     *     ON r.REVIEW_ID = e.REVIEW_ID
     *     LEFT JOIN #BLOCK b
     *     ON  b.START_DT <= e.CREATE_DT  AND e.CREATE_DT <= b.END_DT
     *     WHERE e.CREATE_DT BETWEEN  '1999-01-01' AND '2022-05-06' AND  r.USER_SUID = 'YO2022042527090787' AND BLOCK_SUID IS NULL
     *     GROUP BY e.WORLD_ID, e.REVIEW_ID , e.USER_SUID, e.CREATE_DT
     *
     *     SELECT notis.REVIEW_ID, notis.WORLD_ID,
     *         u.[USER_ID], w.NAME, place.PLACE_ID, place.X, place.Y, notis.CREATE_DT
     *     FROM #EMOJI_NOTIS notis
     *     LEFT JOIN
     * Name        :
     * Author      : 조 준 희
     * History     : [2022/04/27] - 조 준 희 - Create
     */
    @Override
    public List<EmojiNotiDto> findEmojiNotis(String suid) {


        List<EmojiNotiDto> notis = new ArrayList<>();

        String sql = "\n" +
                "CREATE TABLE #EMOJI_NOTIS(\n" +
                "    WORLD_ID BIGINT,\n" +
                "    REVIEW_ID BIGINT,\n" +
                "    USER_SUID VARCHAR(18),\n" +
                "    CREATE_DT DATETIME \n" +
                ")\n" +

                "CREATE TABLE #BLOCK(\n" +
                "    BLOCK_SUID VARCHAR(18),\n" +
                "    START_DT DATETIME,\n" +
                "    END_DT DATETIME\n" +
                ")\n" +

                "INSERT INTO #BLOCK\n" +
                "SELECT BLOCK_SUID, CREATE_DT, CASE WHEN IS_BLOCKING = 'Y' THEN '2999-01-01' ELSE UPDATE_DT END\n" +
                "  FROM USER_BLOCK_LOG \n" +
                " WHERE USER_SUID = ? \n" +

                "INSERT INTO #EMOJI_NOTIS\n" +
                "SELECT e.WORLD_ID, e.REVIEW_ID , e.USER_SUID, e.CREATE_DT \n" +
                "  FROM  EMOJI_STATUS e\n" +
                " INNER JOIN REVIEW r\n" +
                "    ON r.REVIEW_ID = e.REVIEW_ID\n" +
                "  LEFT JOIN #BLOCK b\n" +
                "    ON  b.START_DT <= e.CREATE_DT  AND e.CREATE_DT <= b.END_DT\n" +
                " WHERE e.CREATE_DT BETWEEN  ? AND ? AND  r.USER_SUID = ? AND BLOCK_SUID IS NULL\n" +
                " GROUP BY e.WORLD_ID, e.REVIEW_ID , e.USER_SUID, e.CREATE_DT \n" +

                "  SELECT notis.REVIEW_ID, notis.WORLD_ID, \n" +
                "        u.[USER_ID], w.NAME, place.PLACE_ID, place.X, place.Y, notis.CREATE_DT\n" +
                "   FROM #EMOJI_NOTIS notis\n" +
                "  LEFT JOIN REVIEW r\n" +
                "    ON notis.REVIEW_ID = r.REVIEW_ID\n" +
                "  LEFT JOIN PLACE place\n" +
                "    ON place.PLACE_ID = r.PLACE_ID\n" +
                "  LEFT JOIN USER_INFO u\n" +
                "    ON notis.USER_SUID = u.SUID\n" +
                "  LEFT JOIN WORLD w\n" +
                "    ON notis.WORLD_ID = w.WORLD_ID " +
                " ORDER BY notis.CREATE_DT DESC \n" +

                "DROP TABLE #BLOCK \n" +
                "DROP TABLE #EMOJI_NOTIS";

         Query nativeQuery = entityManager.createNativeQuery(sql)
                                .setParameter(1, suid)
                                .setParameter(2,LocalDateTime.now().minusWeeks(12))
                                .setParameter(3, LocalDateTime.now())
                                .setParameter(4, suid);

        List<Object[]> result =  nativeQuery.getResultList();

        for(Object[] obj : result){

            Timestamp notidate = new Timestamp(0);
            if( obj[7] instanceof Timestamp )
                notidate= (Timestamp)obj[7];

            notis.add(EmojiNotiDto.builder().reviewId(Long.parseLong(obj[0].toString()))
                            .worldId(Long.parseLong(obj[1].toString()))
                            .userId(obj[2].toString())
                            .worldName(obj[3].toString())
                            .placeId(obj[4].toString())
                            .x(BigDecimalParser.parse(obj[5].toString()))
                            .y(BigDecimalParser.parse(obj[6].toString()))
                            .notiDate( notidate.toLocalDateTime() )
                    .build());
        }
        return notis;
    }
}
