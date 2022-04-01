package com.map.mutual.side.review.repository.dsl.impl;

import com.map.mutual.side.review.model.dto.QReviewDto;
import com.map.mutual.side.review.model.dto.ReviewDto;
import com.map.mutual.side.review.model.entity.QReviewEntity;
import com.map.mutual.side.review.model.entity.QReviewWorldMappingEntity;
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
    public List<ReviewDto> findAllReviewsByWorldId(Long worldId) {
        QReviewEntity qReview = new QReviewEntity("qReview");
        QReviewWorldMappingEntity qRW = new QReviewWorldMappingEntity("qRW");
        List<ReviewDto> reviewDtos = jpaQueryFactory.select(new QReviewDto
                (qReview.userEntity,
                                qReview.title,
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
}
