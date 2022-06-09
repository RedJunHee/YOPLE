package com.map.mutual.side.review.repository;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.review.model.entity.PlaceEntity;
import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.map.mutual.side.review.repository.dsl.ReviewRepoDSL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<ReviewEntity, Long>, ReviewRepoDSL {
    ReviewEntity findByReviewId(Long reviewId);
    void deleteAllByUserEntity(UserEntity userEntity);
    List<ReviewEntity> findAllByUserEntity(UserEntity userEntity);
    boolean existsByUserEntityAndPlaceEntity(UserEntity userEntity, PlaceEntity placeEntity);
}
