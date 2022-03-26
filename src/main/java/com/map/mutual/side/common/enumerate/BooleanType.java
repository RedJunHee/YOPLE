package com.map.mutual.side.common.enumerate;

import com.map.mutual.side.review.model.enumeration.EmojiType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Class       : BooleanType
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-11] - 조 준희 - Class Create
 */
@Getter
@AllArgsConstructor
public enum BooleanType {
    NONE("NONE"),
    Y("ABLE"),
    N("DISABLE");

    private String status;

    public static BooleanType find(String dbData) {
        return Arrays.stream(values())
                .filter(booleanType ->
                        booleanType.status.equals(dbData))
                .findAny()
                .orElse(NONE);
    }

}
