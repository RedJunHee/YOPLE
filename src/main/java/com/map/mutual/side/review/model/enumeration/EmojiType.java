package com.map.mutual.side.review.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum EmojiType {
    GOOD("GOOD", ""),
    LIKE("LIKE", ""),
    HATE("HATE", ""),
    LAUGH("LAUGH", ""),
    NONE("NONE", "");

    private String type;
    private String img_url;


    public static EmojiType find(String dbData) {
        return Arrays.stream(values())
                .filter(emojiType ->
                        emojiType.type.equals(dbData))
                .findAny()
                .orElse(NONE);
    }


}
