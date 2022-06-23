package com.map.mutual.side.review.repository.dsl.impl;

import com.map.mutual.side.auth.model.entity.QUserBlockLogEntity;
import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.review.model.dto.PlaceDetailDto;
import com.map.mutual.side.review.model.dto.QPlaceDetailDto_PlaceDetailInReview;
import com.map.mutual.side.review.model.entity.QReviewEntity;
import com.map.mutual.side.review.model.entity.QReviewWorldMappingEntity;
import com.map.mutual.side.review.repository.ReviewRepo;
import com.map.mutual.side.review.repository.dsl.PlaceRepoDSL;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
public class PlaceRepoDSLImpl implements PlaceRepoDSL {
    private final JPAQueryFactory jpaQueryFactory;
    private final ReviewRepo reviewRepo;
    @Autowired
    public PlaceRepoDSLImpl(JPAQueryFactory jpaQueryFactory, ReviewRepo reviewRepo) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.reviewRepo = reviewRepo;
    }


    @Override
    public List<PlaceDetailDto.PlaceDetailInReview> findPlaceDetailInReview(Long worldId, String placeId, String suid) {

        QReviewEntity qReview = new QReviewEntity("qReview");
        QUserEntity qUser = new QUserEntity("qUser");
        QReviewWorldMappingEntity qReviewWorldMappingEntity = new QReviewWorldMappingEntity("qReviewWorldMappingEntity");
        QUserBlockLogEntity qUserBlockLog = new QUserBlockLogEntity("qUserBlockLog");


        List<PlaceDetailDto.PlaceDetailInReview> results = jpaQueryFactory.select(new QPlaceDetailDto_PlaceDetailInReview(
                qReview.reviewId,
                qReview.imageUrl,
                qUser.profileUrl,
                qUser.profilePinUrl,
                qReview.createTime,
                qReview.userEntity.userId
                ))
                .from(qReview)
                .innerJoin(qUser)
                .on(qReview.userEntity.suid.eq(qUser.suid))
                .innerJoin(qReviewWorldMappingEntity)
                .on(qReview.reviewId.eq(qReviewWorldMappingEntity.reviewEntity.reviewId))
                .where(qReview.userEntity.suid.notIn
                                (JPAExpressions.
                        select(qUserBlockLog.blockSuid)
                        .from(qUserBlockLog)
                        .where(qUserBlockLog.userSuid.eq(suid).and(qUserBlockLog.isBlocking.eq("Y"))))
                        .and(qReviewWorldMappingEntity.worldEntity.worldId.eq(worldId))
                        .and(qReview.placeEntity.placeId.eq(placeId)))
                .orderBy(qReview.createTime.desc())
                .fetch();

        return results;
    }
}
