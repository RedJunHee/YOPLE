package com.map.mutual.side.review.repository.dsl;

import com.map.mutual.side.review.model.dto.PlaceDto;
import com.map.mutual.side.review.model.dto.PlaceRangeDto;
import com.map.mutual.side.world.model.dto.WorldDto;

import java.util.List;

public interface ReviewWorldMappingRepoDSL {
    List<WorldDto> findAllWorldsByReviewId (Long reviewId, String suid);
    List<PlaceDto.PlaceSimpleDto> findRangePlaces(PlaceRangeDto placeRangeDto);
}
