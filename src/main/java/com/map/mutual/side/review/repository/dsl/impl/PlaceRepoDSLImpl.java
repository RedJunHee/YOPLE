package com.map.mutual.side.review.repository.dsl.impl;

import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.auth.model.entity.QUserWorldInvitingLogEntity;
import com.map.mutual.side.review.model.dto.PlaceDetailDto;
import com.map.mutual.side.review.model.dto.QPlaceDetailDto_tempReview;
import com.map.mutual.side.review.model.entity.QReviewEntity;
import com.map.mutual.side.review.model.entity.QReviewWorldMappingEntity;
import com.map.mutual.side.review.repository.dsl.PlaceRepoDSL;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

    public PlaceRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public List<PlaceDetailDto.tempReview> findPlaceDetails(Long worldId, Long placeId) {
        QReviewEntity qReview = new QReviewEntity("qReview");
        QReviewWorldMappingEntity qRW = new QReviewWorldMappingEntity("qRW");
        QUserWorldInvitingLogEntity qLog = new QUserWorldInvitingLogEntity("qLog");
        QUserEntity qUser = new QUserEntity("qUser");

        List<PlaceDetailDto.tempReview> results = jpaQueryFactory.select(new QPlaceDetailDto_tempReview(
                qReview.reviewId,
                qReview.title,
                qReview.content,
                qReview.imageUrl,
                qReview.userEntity.suid,
                qUser.userId,
                qReview.updateTime
                )).distinct()
                .from(qRW)
                .join(qReview)
                .on(qReview.reviewId.eq(qRW.reviewEntity.reviewId))
                .join(qLog)
                .on(qReview.userEntity.suid.eq(qLog.targetSuid))
                .join(qUser)
                .on(qUser.suid.eq(qLog.userSuid))
                .where(qRW.worldEntity.worldId.eq(worldId)
                        .and(qReview.placeEntity.placeId.eq(placeId)))
                .orderBy(qReview.updateTime.desc())
                .fetch();
        return results;
    }
}
