package com.map.mutual.side.review.model.keys;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
/**
 * fileName       : EmojiStatusEntityKeys
 * author         : kimjaejung
 * createDate     : 2022/03/25
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/25        kimjaejung       최초 생성
 *
 */
@EqualsAndHashCode
public class EmojiStatusEntityKeys implements Serializable {
    private String userSuid;
    private Long worldId;
    private Long reviewId;
    private Long emojiId;
}
