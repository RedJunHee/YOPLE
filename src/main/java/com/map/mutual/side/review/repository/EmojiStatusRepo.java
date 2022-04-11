package com.map.mutual.side.review.repository;

import com.map.mutual.side.review.model.entity.EmojiEntity;
import com.map.mutual.side.review.model.entity.EmojiStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmojiStatusRepo extends JpaRepository<EmojiStatusEntity, Long> {
    boolean existsByUserSuidAndWorldIdAndReviewIdAndEmojiEntity(String userSuid, Long worldId, Long reviewId, EmojiEntity emojiEntity);
}