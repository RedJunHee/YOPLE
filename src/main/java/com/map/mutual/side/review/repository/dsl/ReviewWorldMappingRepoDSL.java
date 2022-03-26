package com.map.mutual.side.review.repository.dsl;

import com.map.mutual.side.review.model.entity.ReviewEntity;

import java.util.List;

public interface ReviewWorldMappingRepoDSL {
    List<ReviewEntity> findAllReviewsByWorldId(Long worldId);
}
