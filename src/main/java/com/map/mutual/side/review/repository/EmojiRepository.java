package com.map.mutual.side.review.repository;

import com.map.mutual.side.review.model.entity.EmojiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmojiRepository extends JpaRepository<EmojiEntity, Long> {
}
