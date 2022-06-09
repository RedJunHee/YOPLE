package com.map.mutual.side.review.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum EmojiType {

    NONE(0L,"NONE", "NONE", ActiveType.Y),

    /**
     * //가고싶어요, 도움 됐어요, 같이 가요, 좋아요
     */
    GOOD(1L,"value1", "1", ActiveType.Y),
    LIKE(2L,"value2", "2", ActiveType.Y),
    HATE(3L,"value3", "3", ActiveType.Y),
    LAUGH(4L,"value4", "4", ActiveType.Y);

    private final Long id;
    private final String value;
    private final String img_url;
    private final String activeType;

    public static final int EMOJI_NUM = 4;


    public static class ActiveType {
        public static final String Y = "ACTIVE";
        public static final String N = "INACTIVE";
    }



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
