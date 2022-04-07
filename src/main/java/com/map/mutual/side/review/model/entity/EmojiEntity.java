package com.map.mutual.side.review.model.entity;

import com.map.mutual.side.common.enumerate.BooleanType;
import com.map.mutual.side.common.repository.config.TimeEntity;
import com.map.mutual.side.review.model.converter.BooleanTypeConverter;
import com.map.mutual.side.review.model.converter.EmojiTypeConverter;
import com.map.mutual.side.review.model.enumeration.EmojiType;
import lombok.*;

import javax.persistence.*;
/**
 * fileName       : EmojiEntity
 * author         : kimjaejung
 * createDate     : 2022/03/22
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/22        kimjaejung       최초 생성
 *
 */
@Entity
@Table(name = "EMOJI")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmojiEntity extends TimeEntity {
    @Id
    @Column(name="EMOJI_ID", nullable = false, updatable = false, columnDefinition = "BIGINT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emojiId;

    @Convert(converter = EmojiTypeConverter.class)
    @Column(name="EMOJI_VALUE", nullable = false, columnDefinition = "VARCHAR(200)")
    private EmojiType emojiValue;

    @Column(name="EMOJI_IMG", columnDefinition = "VARCHAR(200)")
    private String emojiImg;

    @Convert(converter = BooleanTypeConverter.class)
    @Column(name="EMOJI_STATUS", nullable = false, columnDefinition = "VARCHAR(20)")
    private BooleanType emojiStatus;

}
