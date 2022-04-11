package com.map.mutual.side.review.repository.dsl.impl;

import com.map.mutual.side.review.model.dto.PlaceDto;
import com.map.mutual.side.review.model.dto.PlaceRangeDto;
import com.map.mutual.side.review.model.dto.QReviewDto;
import com.map.mutual.side.review.model.dto.ReviewDto;
import com.map.mutual.side.review.model.entity.QReviewEntity;
import com.map.mutual.side.review.model.entity.QReviewWorldMappingEntity;
import com.map.mutual.side.review.repository.dsl.ReviewWorldMappingRepoDSL;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.model.entity.QWorldEntity;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
import com.map.mutual.side.world.model.entity.WorldEntity;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewWorldMappingRepoDSLImpl implements ReviewWorldMappingRepoDSL {
    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    public ReviewWorldMappingRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<ReviewDto> findAllReviewsByWorldId(Long worldId) {
        QReviewEntity qReview = new QReviewEntity("qReview");
        QReviewWorldMappingEntity qRW = new QReviewWorldMappingEntity("qRW");
        List<ReviewDto> reviewDtos = jpaQueryFactory.select(new QReviewDto
                        (qReview.userEntity,
                                qReview.content,
                                qReview.imageUrl,
                                qReview.reviewId))
                .from(qReview)
                .join(qRW)
                .on(qReview.reviewId.eq(qRW.reviewEntity.reviewId))
                .where(qRW.worldEntity.worldId.eq(worldId))
                .fetch();
        return reviewDtos;
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
        List<PlaceDto.PlaceSimpleDto> result = new ArrayList<>();

        String sql = "SELECT p.PLACE_ID , p.NAME, p.X , p.Y , ui.PROFILE_URL" +
                " FROM (" +
                "SELECT ROW_NUMBER() OVER(PARTITION BY r.PLACE_ID ORDER BY m.CREATE_DT DESC) as NUM" +
                " , r.PLACE_ID, r.REVIEW_ID, m.WORLD_ID, r.USER_SUID " +
                " FROM REVIEW_WORLD_MAPPING m" +
                " INNER JOIN REVIEW r" +
                "  ON m.REVIEW_ID = r.REVIEW_ID" +
                " WHERE WORLD_ID = "+ placeRangeDto.getWorldId() +
                ") as subrow " +
                "LEFT JOIN PLACE p " +
                "ON p.PLACE_ID = subrow.PLACE_ID " +
                "LEFT JOIN USER_INFO ui " +
                "ON subrow.USER_SUID = ui.SUID " +
                "WHERE subrow.num = 1 AND p.X BETWEEN "+placeRangeDto.getX_start()+" AND "+placeRangeDto.getX_end()+" AND p.Y BETWEEN "+placeRangeDto.getY_start()+" AND "+placeRangeDto.getY_end();

        List<Object[]> dtos = entityManager.createNativeQuery(sql).getResultList();
        dtos.forEach(data -> {
                        PlaceDto.PlaceSimpleDto placeInRange = PlaceDto.PlaceSimpleDto.builder()
                    .placeId(data[0].toString())
                    .name(data[1].toString())
                    .x((BigDecimal)data[2])
                    .y((BigDecimal)data[3])
                    .profileUrl(data[4].toString())
                    .build();
            result.add(placeInRange);
        });
        return result;
    }

}
