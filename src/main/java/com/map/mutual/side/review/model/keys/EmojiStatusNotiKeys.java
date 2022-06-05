package com.map.mutual.side.review.model.keys;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
/**
 * fileName       : EmojiStatusNotiKeys
 * author         : kimjaejung
 * createDate     : 2022/05/04
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/05/04        kimjaejung       최초 생성
 *
 */
@EqualsAndHashCode
public class EmojiStatusNotiKeys implements Serializable {
    private String userSuid;
    private Long worldId;
    private Long reviewId;
}
