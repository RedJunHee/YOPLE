package com.map.mutual.side.review.repository.dsl.impl;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.review.model.dto.PlaceDto;
import com.map.mutual.side.review.model.dto.PlaceRangeDto;
import com.map.mutual.side.review.model.entity.QReviewWorldMappingEntity;
import com.map.mutual.side.review.repository.dsl.ReviewWorldMappingRepoDSL;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.model.entity.QWorldEntity;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
import com.map.mutual.side.world.model.entity.WorldEntity;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewWorldMappingRepoDSLImpl implements ReviewWorldMappingRepoDSL {
    private final JPAQueryFactory jpaQueryFactory;


    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    public ReviewWorldMappingRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    //월드
    @Override
    public List<WorldDto> findAllWorldsByReviewId(Long reviewId, String suid) {

        List<WorldEntity> worldEntities = jpaQueryFactory
                .select(QWorldEntity.worldEntity)
                .from(QReviewWorldMappingEntity.reviewWorldMappingEntity)
                .innerJoin(QWorldEntity.worldEntity)
                .on(QReviewWorldMappingEntity.reviewWorldMappingEntity.reviewEntity.reviewId.eq(reviewId)
                        .and(QReviewWorldMappingEntity.reviewWorldMappingEntity.worldEntity.worldId.eq(QWorldEntity.worldEntity.worldId)))
                .fetchJoin()
                .leftJoin(QWorldUserMappingEntity.worldUserMappingEntity)
                .on(QWorldEntity.worldEntity.worldId.eq(QWorldUserMappingEntity.worldUserMappingEntity.worldId)
                        .and(QWorldUserMappingEntity.worldUserMappingEntity.userSuid.eq(suid)))
                .orderBy(new CaseBuilder().when(QWorldEntity.worldEntity.worldOwner.eq(suid)).then(1).otherwise(0).desc()
                        , QWorldUserMappingEntity.worldUserMappingEntity.accessTime.desc())
                .fetch();

        List<WorldDto> worlds = new ArrayList<>();

        for (WorldEntity world : worldEntities) {
            worlds.add(WorldDto.builder().worldId(world.getWorldId()).worldName(world.getWorldName()).build());
        }
        return worlds;
    }

    @Override
    public List<PlaceDto.PlaceSimpleDto> findRangePlaces(PlaceRangeDto placeRangeDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        List<PlaceDto.PlaceSimpleDto> result = new ArrayList<>();

        String sql = "SELECT p.PLACE_ID , p.NAME, p.X , p.Y , ui.PROFILE_PIN_URL, subrow.CREATE_DT" +
                " FROM (" +
                "SELECT ROW_NUMBER() OVER(PARTITION BY r.PLACE_ID ORDER BY m.CREATE_DT DESC) as NUM" +
                " , r.PLACE_ID, r.REVIEW_ID, m.WORLD_ID, r.USER_SUID, r.CREATE_DT " +
                " FROM REVIEW_WORLD_MAPPING m" +
                " INNER JOIN REVIEW r" +
                "  ON m.REVIEW_ID = r.REVIEW_ID" +
                " WHERE (r.USER_SUID not in ( SELECT UBL.BLOCK_SUID FROM USER_BLOCK_LOG UBL WHERE UBL.USER_SUID=\'"+ userInfoDto.getSuid() + "\' AND UBL.IS_BLOCKING='Y'))" +
                " AND WORLD_ID = "+ placeRangeDto.getWorldId() +
                ") as subrow " +
                "LEFT JOIN PLACE p " +
                "ON p.PLACE_ID = subrow.PLACE_ID " +
                "LEFT JOIN USER_INFO ui " +
                "ON subrow.USER_SUID = ui.SUID " +
                "WHERE subrow.num = 1 AND p.X BETWEEN "+placeRangeDto.getX_start()+" AND "+placeRangeDto.getX_end()+" AND p.Y BETWEEN "+placeRangeDto.getY_start()+" AND "+placeRangeDto.getY_end();

        List<Object[]> dtos = entityManager.createNativeQuery(sql).getResultList();
        dtos.forEach(data -> {
            PlaceDto.PlaceSimpleDto placeInRange;
            if(data[4] == null) {
                placeInRange = PlaceDto.PlaceSimpleDto.builder()
                        .placeId(data[0].toString())
                        .name(data[1].toString())
                        .x((BigDecimal)data[2])
                        .y((BigDecimal)data[3])
                        .profilePinUrl(null)
                        .createDt(((Timestamp) data[5]).toLocalDateTime())
                        .build();

            } else {
                placeInRange = PlaceDto.PlaceSimpleDto.builder()
                        .placeId(data[0].toString())
                        .name(data[1].toString())
                        .x((BigDecimal)data[2])
                        .y((BigDecimal)data[3])
                        .profilePinUrl(data[4].toString())
                        .createDt(((Timestamp) data[5]).toLocalDateTime())
                        .build();

            }
            result.add(placeInRange);
        });
        return result;
    }

}
