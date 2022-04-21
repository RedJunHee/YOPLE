package com.map.mutual.side.auth.model.dto.block;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class       : UserBlockedDto
 * Author      : 조 준 희
 * Description : 사용자 차단리스트 조회
 * History     : [2022-04-21] - 조 준희 - Class Create
 */
@Getter
@NoArgsConstructor
public class UserBlockedDto {
    private Long blockId;
    private String userId;
    private String name;
    private String profileUrl;

    @QueryProjection
    @Builder
    public UserBlockedDto(Long blockId, String userId, String name, String profileUrl) {
        this.blockId = blockId;
        this.userId = userId;
        this.name = name;
        this.profileUrl = profileUrl;
    }
}
