package com.map.mutual.side.review.repository;

import com.map.mutual.side.review.model.entity.EmojiStatusNotiEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * fileName       : EmojiStatusNotiRepo
 * author         : kimjaejung
 * createDate     : 2022/05/04
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/05/04        kimjaejung       최초 생성
 *
 */
public interface EmojiStatusNotiRepo extends JpaRepository<EmojiStatusNotiEntity, String> {
    boolean existsByUserSuidAndWorldIdAndReviewId(String userSuid, long worldId, long reviewId);
}
