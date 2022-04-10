package com.map.mutual.side.review.repository;

import com.map.mutual.side.review.model.entity.PlaceEntity;
import com.map.mutual.side.review.repository.dsl.PlaceRepoDSL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * fileName       : PlaceRepo
 * author         : kimjaejung
 * createDate     : 2022/04/05
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/04/05        kimjaejung       최초 생성
 *
 */
@Repository
public interface PlaceRepo extends JpaRepository<PlaceEntity, String>, PlaceRepoDSL {
    PlaceEntity findByPlaceId(String placeId);
}
