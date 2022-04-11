package com.map.mutual.side.review.repository.dsl.impl;

import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.review.model.dto.PlaceDetailDto;
import com.map.mutual.side.review.model.dto.QPlaceDetailDto_TempReview;
import com.map.mutual.side.review.model.entity.QReviewEntity;
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
    public List<PlaceDetailDto.TempReview> findPlaceDetails(Long worldId, String placeId) {
        QReviewEntity qReview = new QReviewEntity("qReview");
        QUserEntity qUser = new QUserEntity("qUser");

        List<PlaceDetailDto.TempReview> results = jpaQueryFactory.select(new QPlaceDetailDto_TempReview(
                qReview.reviewId,
                qReview.imageUrl,
                qUser.profileUrl,
                qReview.createTime
                ))
                .from(qReview)
                .join(qUser)
                .on(qReview.userEntity.suid.eq(qUser.suid))
                .orderBy(qReview.createTime.desc())
                .fetch();
        return results;
    }
}
