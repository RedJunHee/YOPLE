package com.map.mutual.side.review.repository.dsl;

import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.model.entity.WorldEntity;
import com.map.mutual.side.review.model.dto.ReviewDto;

import java.util.List;

public interface ReviewWorldMappingRepoDSL {
    List<ReviewDto> findAllReviewsByWorldId(Long worldId);
    List<ReviewEntity> findAllReviewsByWorldId(Long worldId);
    List<WorldDto> findAllWorldsByReviewId (Long reviewId, String suid);
}
