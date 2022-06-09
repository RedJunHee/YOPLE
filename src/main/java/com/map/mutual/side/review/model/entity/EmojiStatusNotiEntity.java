package com.map.mutual.side.review.model.entity;

import com.map.mutual.side.common.repository.config.CreateDtEntity;
import com.map.mutual.side.review.model.keys.EmojiStatusNotiKeys;
import lombok.*;

import javax.persistence.*;
/**
 * fileName       : EmojiStatusNotiEntity
 * author         : kimjaejung
 * createDate     : 2022/05/04
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/05/04        kimjaejung       최초 생성
 *
 */
@Entity
@Table(name = "EMOJI_STATUS_NOTI")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@IdClass(EmojiStatusNotiKeys.class)
public class EmojiStatusNotiEntity extends CreateDtEntity {
    @Id
    @Column(name="USER_SUID", insertable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Id
    @Column(name = "WORLD_ID", nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long worldId;

    @Id
    @Column(name="REVIEW_ID",insertable = false, updatable = false, columnDefinition = "BIGINT")
    private Long reviewId;
}
