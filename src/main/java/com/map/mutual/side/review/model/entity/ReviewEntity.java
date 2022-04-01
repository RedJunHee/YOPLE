package com.map.mutual.side.review.model.entity;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.common.repository.config.TimeEntity;
import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.List;

/**
 * fileName       : ReviewEntity
 * author         : kimjaejung
 * createDate     : 2022/03/21
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/21        kimjaejung       최초 생성
 *
 */

@Entity
@Table(name = "REVIEW")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReviewEntity extends TimeEntity implements Persistable<Long> {
    @Id
    @Column(name="REVIEW_ID", nullable = false, insertable = false, updatable = false, columnDefinition = "BIGINT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_SUID", referencedColumnName = "SUID")
    private UserEntity userEntity;

    @Column(name="TITLE", columnDefinition = "VARCHAR(100)")
    private String title;

    @Column(name="CONTENT", columnDefinition = "VARCHAR(1000)")
    private String content;

    @Column(name="IMG_URL", columnDefinition = "VARCHAR(100)")
    private String imageUrl;

    @OneToMany(mappedBy = "reviewEntity")
    private List<ReviewWorldMappingEntity> reviewWorldMappingEntities;

    @Override
    public Long getId() {
        return reviewId;
    }

    @Override
    public boolean isNew() {
        return getCreateTime() == null;
    }
}
