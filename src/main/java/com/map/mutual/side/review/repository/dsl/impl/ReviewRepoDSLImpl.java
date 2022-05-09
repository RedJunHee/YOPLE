package com.map.mutual.side.review.repository.dsl.impl;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.QUserBlockLogEntity;
import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.review.model.dto.QReviewDto_ReviewWithInviterDto;
import com.map.mutual.side.review.model.dto.ReviewDto;
import com.map.mutual.side.review.model.entity.EmojiStatusEntity;
import com.map.mutual.side.review.model.entity.QReviewEntity;
import com.map.mutual.side.review.model.entity.QReviewWorldMappingEntity;
import com.map.mutual.side.review.model.enumeration.EmojiType;
import com.map.mutual.side.review.repository.EmojiStatusRepo;
import com.map.mutual.side.review.repository.dsl.ReviewRepoDSL;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
    private final EmojiStatusRepo emojiStatusRepo;

    public ReviewRepoDSLImpl(JPAQueryFactory jpaQueryFactory, EmojiStatusRepo emojiStatusRepo) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.emojiStatusRepo = emojiStatusRepo;
    }


    @Override
    public ReviewDto.ReviewWithInviterDto findByReviewWithInviter(Long reviewId, Long worldId) throws YOPLEServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        QReviewEntity qReview = new QReviewEntity("qReview");
        QReviewWorldMappingEntity qReviewWorldMappingEntity = new QReviewWorldMappingEntity("qReviewWorldMappingEntity");
        QWorldUserMappingEntity qWorldUserMappingEntity1 = new QWorldUserMappingEntity("qWorldUserMappingEntity1");
        QWorldUserMappingEntity qWorldUserMappingEntity2 = new QWorldUserMappingEntity("qWorldUserMappingEntity2");
        QUserEntity qUser1 = new QUserEntity("qUser1");
        QUserEntity qUser2 = new QUserEntity("qUser2");
        QUserBlockLogEntity qUserBlockLog = new QUserBlockLogEntity("qUserBlockLog");

        ReviewDto.ReviewWithInviterDto result = jpaQueryFactory.select(new QReviewDto_ReviewWithInviterDto(
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
                        .and(qWorldUserMappingEntity1.worldEntity.worldId.eq(worldId))
                        .and(qReview.userEntity.suid.notIn(JPAExpressions.select(qUserBlockLog.blockSuid).from(qUserBlockLog).where(qUserBlockLog.userSuid.eq(userInfoDto.getSuid())))))
                .fetchOne();

        //Emoji 끌어오기.
        List<ReviewDto.ReviewWithInviterDto.TempEmoji> emojis = new ArrayList<>();
        List<EmojiStatusEntity> emojiStatusEntityList = emojiStatusRepo.findAllByReviewIdAndWorldId(reviewId, worldId);
        for (int emojiId = 1; emojiId <= EmojiType.EMOJI_NUM; emojiId++) {
            Long emojiNum = (long) emojiId;
            if (emojiStatusEntityList.stream().noneMatch(data -> data.getEmojiEntity().getEmojiId().getId().equals(emojiNum))) {

            } else {
                ReviewDto.ReviewWithInviterDto.TempEmoji emoji = ReviewDto.ReviewWithInviterDto.TempEmoji.builder()
                        .emojiType(emojiNum)
                        .typeQuantity(emojiStatusEntityList.stream().filter(data -> Objects.equals(data.getEmojiEntity().getEmojiId().getId(), emojiNum)).count())
                        .isChecked(emojiStatusEntityList.stream().filter(data -> data.getEmojiEntity().getEmojiId().getId().equals(emojiNum)).anyMatch(data -> data.getUserSuid().equals(userInfoDto.getSuid())))
                        .createdDt(emojiStatusEntityList.stream().filter(data -> data.getEmojiEntity().getEmojiId().getId().equals(emojiNum)).min(Comparator.comparing(EmojiStatusEntity::getWorldId)).get().getCreateTime())
                        .build();

                emojis.add(emoji);
            }
        }
        if (result == null) {
            throw new YOPLEServiceException(ApiStatusCode.THIS_REVIEW_IS_BLOCK_USERS_REVIEW);
        }
        result.setEmoji(emojis);
        return result;
    }

    @Override
    public String findByReviewOwnerFcmToken(Long reviewId) {
        QReviewEntity qReview = new QReviewEntity("qReview");
        QUserEntity qUser1 = new QUserEntity("qUser1");
        String result = jpaQueryFactory
                .select(qUser1.fcmToken)
                .from(qUser1)
                .innerJoin(qReview)
                .on(qUser1.suid.eq(qReview.userEntity.suid))
                .where(qReview.reviewId.eq(reviewId))
                .fetchOne();
        return result;
    }
}
