package com.map.mutual.side.review.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum EmojiType {
    NONE(0L,"NONE", "NONE", ActiveType.Y),

    GOOD(1L,"GOOD", "1", ActiveType.Y),
    LIKE(2L,"LIKE", "2", ActiveType.Y),
    HATE(3L,"HATE", "3", ActiveType.Y),
    LAUGH(4L,"LAUGH", "4", ActiveType.Y);

    private final Long id;
    private final String value;
    private final String img_url;
    private final String activeType;

    public static class ActiveType {
        public static final String Y = "ACTIVE";
        public static final String N = "INACTIVE";
    }

    public static final int EMOJI_NUM = 4;


    public static EmojiType findValue(String dbData) {
        return Arrays.stream(values())
                .filter(emojiType ->
                        emojiType.getValue().equals(dbData))
                .findAny()
                .orElse(NONE);
    }

    public static EmojiType findImg(String dbData) {
        return Arrays.stream(values())
                .filter(emojiType ->
                        emojiType.getImg_url().equals(dbData))
                .findAny()
                .orElse(NONE);
    }
    public static EmojiType findActiveType(String dbData) {
        return Arrays.stream(values())
                .filter(emojiType ->
                        emojiType.getActiveType().equals(dbData))
                .findAny()
                .orElse(NONE);
    }

    public static EmojiType findId(Long dbData) {
        return Arrays.stream(values())
                .filter(emojiType ->
                        emojiType.getId().equals(dbData))
                .findAny()
                .orElse(NONE);
    }
}
