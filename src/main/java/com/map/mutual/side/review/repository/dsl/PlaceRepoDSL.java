package com.map.mutual.side.review.repository.dsl;

import com.map.mutual.side.review.model.dto.PlaceDetailDto;

import java.util.List;

/**
 * fileName       : PlaceRepoDSL
 * author         : kimjaejung
 * createDate     : 2022/04/05
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/04/05        kimjaejung       최초 생성
 *
 */
public interface PlaceRepoDSL {
    List<PlaceDetailDto.PlaceDetailInReview> findPlaceDetailInReview(Long worldId, String placeId, String suid);
}
