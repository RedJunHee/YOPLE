package com.map.mutual.side.review.repository;

import com.map.mutual.side.review.model.entity.EmojiEntity;
import com.map.mutual.side.review.model.enumeration.EmojiType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmojiRepo extends JpaRepository<EmojiEntity, Long> {
    EmojiEntity findByEmojiId(EmojiType emojiType);
}
