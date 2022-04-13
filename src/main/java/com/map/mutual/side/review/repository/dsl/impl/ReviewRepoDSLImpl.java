package com.map.mutual.side.review.repository.dsl.impl;

import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.review.model.dto.QReviewDto_ReviewWithInviterDto;
import com.map.mutual.side.review.model.dto.ReviewDto;
import com.map.mutual.side.review.model.entity.QReviewEntity;
import com.map.mutual.side.review.model.entity.QReviewWorldMappingEntity;
import com.map.mutual.side.review.repository.dsl.ReviewRepoDSL;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

/**
 * fileName       : ReviewRepoDSLImpl
 * author         : kimjaejung
 * createDate     : 2022/04/12
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/04/12        kimjaejung       최초 생성
 */
@Repository
public class ReviewRepoDSLImpl implements ReviewRepoDSL {
    private final JPAQueryFactory jpaQueryFactory;

    public ReviewRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public ReviewDto.ReviewWithInviterDto findByReviewWithInviter(Long reviewId, Long worldId) {
        QReviewEntity qReview = new QReviewEntity("qReview");
        QReviewWorldMappingEntity qReviewWorldMappingEntity = new QReviewWorldMappingEntity("qReviewWorldMappingEntity");
        QWorldUserMappingEntity qWorldUserMappingEntity1 = new QWorldUserMappingEntity("qWorldUserMappingEntity1");
        QWorldUserMappingEntity qWorldUserMappingEntity2 = new QWorldUserMappingEntity("qWorldUserMappingEntity2");
        QUserEntity qUser1 = new QUserEntity("qUser1");
        QUserEntity qUser2 = new QUserEntity("qUser2");


        ReviewDto.ReviewWithInviterDto result =  jpaQueryFactory.select(new QReviewDto_ReviewWithInviterDto(
                        qReview.reviewId,
                        qReview.content,
                        qReview.imageUrl,
                        qUser1.userId,
                        qUser2.userId,
                        qReview.createTime
                ))
                .from(qReview)
                .innerJoin(qReviewWorldMappingEntity)
                .on(qReview.reviewId.eq(qReviewWorldMappingEntity.reviewEntity.reviewId))
                .leftJoin(qWorldUserMappingEntity1)
                .on(qWorldUserMappingEntity1.userEntity.suid.eq(qReview.userEntity.suid))
                .leftJoin(qWorldUserMappingEntity2)
                .on(qWorldUserMappingEntity2.worldUserCode.eq(qWorldUserMappingEntity1.worldinvitationCode))
                .leftJoin(qUser1)
                .on(qWorldUserMappingEntity1.userEntity.suid.eq(qUser1.suid))
                .leftJoin(qUser2)
                .on(qWorldUserMappingEntity2.userEntity.suid.eq(qUser2.suid))
                .where(qReviewWorldMappingEntity.reviewEntity.reviewId.eq(reviewId)
                        .and(qReviewWorldMappingEntity.worldEntity.worldId.eq(worldId))
                        .and(qWorldUserMappingEntity1.worldEntity.worldId.eq(worldId)))
                .fetchOne();

        return result;
    }
}
