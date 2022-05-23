package com.map.mutual.side.review.repository;

import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.map.mutual.side.review.repository.dsl.ReviewRepoDSL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepo extends JpaRepository<ReviewEntity, Long>, ReviewRepoDSL {
    ReviewEntity findByReviewId(Long reviewId);
}
