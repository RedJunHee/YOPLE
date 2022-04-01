package com.map.mutual.side.review.repository;

import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.map.mutual.side.review.model.entity.ReviewWorldMappingEntity;
import com.map.mutual.side.review.repository.dsl.ReviewWorldMappingRepoDSL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ReviewWorldMappingRepository extends JpaRepository<ReviewWorldMappingEntity, Long>, ReviewWorldMappingRepoDSL {
    List<ReviewWorldMappingEntity> findAllByReviewEntity(ReviewEntity reviewId);
    @Transactional
    void deleteAllByReviewEntity(ReviewEntity reviewId);
}
