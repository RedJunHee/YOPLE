package com.map.mutual.side.review.repository.dsl;

import com.map.mutual.side.review.model.dto.ReviewDto;
import com.map.mutual.side.world.model.dto.WorldDto;

import java.util.List;

public interface ReviewWorldMappingRepoDSL {
    List<ReviewDto> findAllReviewsByWorldId(Long worldId);
    List<WorldDto> findAllWorldsByReviewId (Long reviewId, String suid);
    List<ReviewDto> findAllReviewsAndIMG(Long worldId);
}
