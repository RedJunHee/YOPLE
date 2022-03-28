package com.map.mutual.side.review.model.keys;

import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.map.mutual.side.world.model.entity.WorldEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
/**
 * fileName       : ReviewWorldMappingEntityKeys
 * author         : kimjaejung
 * createDate     : 2022/03/25
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/25        kimjaejung       최초 생성
 *
 */
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewWorldMappingEntityKeys implements Serializable {
    private Long reviewEntity;
    private Long worldEntity;
}
