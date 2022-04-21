package com.map.mutual.side.review.model.entity;

import com.map.mutual.side.common.repository.config.CreateDtEntity;
import com.map.mutual.side.review.model.keys.EmojiStatusEntityKeys;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "EMOJI_STATUS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@IdClass(EmojiStatusEntityKeys.class)
public class EmojiStatusEntity extends CreateDtEntity {
    @Id
    @Column(name="USER_SUID", insertable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Id
    @Column(name = "WORLD_ID", nullable = false, updatable = false, columnDefinition = "BIGINT")
    private Long worldId;

    @Id
    @Column(name="REVIEW_ID",insertable = false, updatable = false, columnDefinition = "BIGINT")
    private Long reviewId;

    @Id
    @Column(name="EMOJI_ID",insertable = false, updatable = false, columnDefinition = "BIGINT")
    private Long emojiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = "emojiId")
    @JoinColumn(name = "EMOJI_ID", referencedColumnName = "EMOJI_ID")
    private EmojiEntity emojiEntity;

}
