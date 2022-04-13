package com.map.mutual.side.review.repository;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.map.mutual.side.review.repository.dsl.ReviewRepoDSL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<ReviewEntity, Long>, ReviewRepoDSL {
    ReviewEntity findByReviewId(Long reviewId);
    List<ReviewEntity> findAllByUserEntity(UserEntity userSuid);
}
