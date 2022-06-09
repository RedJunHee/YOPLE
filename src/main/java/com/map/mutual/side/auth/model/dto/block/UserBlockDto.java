package com.map.mutual.side.auth.model.dto.block;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class       : UserBlockDto
 * Author      : 조 준 희
 * Description : 사용자 차단 Dto
 * History     : [2022-04-21] - 조 준희 - Class Create
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBlockDto {
    private String blockSuid;

    public void suidChange(String decodingSuid){
        blockSuid = decodingSuid;
    }

    @Override
    public String toString() {
        return "UserBlockDto{" +
                "blockSuid='" + blockSuid + '\'' +
                '}';
    }
}
