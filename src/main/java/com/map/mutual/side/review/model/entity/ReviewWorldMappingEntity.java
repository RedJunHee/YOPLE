package com.map.mutual.side.review.model.entity;

import com.map.mutual.side.common.repository.config.CreateDtEntity;
import com.map.mutual.side.review.model.keys.ReviewWorldMappingEntityKeys;
import com.map.mutual.side.world.model.entity.WorldEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * fileName       : ReviewWorldMappingEntity
 * author         : kimjaejung
 * createDate     : 2022/03/23
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/23        kimjaejung       최초 생성
 *
 */
@Entity
@Table(name = "REVIEW_WORLD_MAPPING")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@IdClass(ReviewWorldMappingEntityKeys.class)
public class ReviewWorldMappingEntity extends CreateDtEntity {

    @Id
    @Column(name="REVIEW_ID",insertable = false, updatable = false, columnDefinition = "BIGINT")
    private Long reviewId;

    @Id
    @Column(name = "WORLD_ID", insertable = false, updatable = false, columnDefinition = "BIGINT")
    private Long worldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID", referencedColumnName = "REVIEW_ID")
    private ReviewEntity reviewEntity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORLD_ID", referencedColumnName = "WORLD_ID")
    private WorldEntity worldEntity;
}
