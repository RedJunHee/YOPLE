package com.map.mutual.side.review.model.entity;

import com.map.mutual.side.common.repository.config.TimeEntity;
import com.map.mutual.side.review.model.converter.EmojiActiveTypeConverter;
import com.map.mutual.side.review.model.converter.EmojiIdConverter;
import com.map.mutual.side.review.model.converter.EmojiImgConverter;
import com.map.mutual.side.review.model.converter.EmojiValueConverter;
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
    @Convert(converter = EmojiIdConverter.class)
    @Column(name="EMOJI_ID", nullable = false, columnDefinition = "BIGINT")
    private EmojiType emojiId;

    @Convert(converter = EmojiValueConverter.class)
    @Column(name="EMOJI_VALUE", nullable = false, columnDefinition = "VARCHAR(200)")
    private EmojiType emojiValue;

    @Convert(converter = EmojiImgConverter.class)
    @Column(name="EMOJI_IMG", columnDefinition = "VARCHAR(200)")
    private EmojiType emojiImg;

    @Convert(converter = EmojiActiveTypeConverter.class)
    @Column(name="EMOJI_STATUS", nullable = false, columnDefinition = "VARCHAR(20)")
    private EmojiType emojiStatus;

}
