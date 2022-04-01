package com.map.mutual.side.review.repository.dsl;

import com.map.mutual.side.review.model.dto.ReviewDto;

import java.util.List;

public interface ReviewWorldMappingRepoDSL {
    List<ReviewDto> findAllReviewsByWorldId(Long worldId);
}
