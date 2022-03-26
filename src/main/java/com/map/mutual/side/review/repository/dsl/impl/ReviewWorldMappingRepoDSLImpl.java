package com.map.mutual.side.review.repository.dsl.impl;

import com.map.mutual.side.review.model.entity.QReviewEntity;
import com.map.mutual.side.review.model.entity.QReviewWorldMappingEntity;
import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.map.mutual.side.review.repository.dsl.ReviewWorldMappingRepoDSL;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewWorldMappingRepoDSLImpl implements ReviewWorldMappingRepoDSL {
    private final JPAQueryFactory jpaQueryFactory;

    public ReviewWorldMappingRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<ReviewEntity> findAllReviewsByWorldId(Long worldId) {
        List<ReviewEntity> reviewEntities = jpaQueryFactory
                .select(QReviewEntity.reviewEntity)
                .from(QReviewEntity.reviewEntity)
                .join(QReviewWorldMappingEntity.reviewWorldMappingEntity)
                .on(QReviewEntity.reviewEntity.reviewId.eq(QReviewWorldMappingEntity.reviewWorldMappingEntity.reviewId))
                .where(QReviewWorldMappingEntity.reviewWorldMappingEntity.worldId.eq(worldId))
                .fetch();
        return reviewEntities;
    }
}
