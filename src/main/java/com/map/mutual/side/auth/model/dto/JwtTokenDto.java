package com.map.mutual.side.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Description : JWT 응답 DTO
 * Class       : JwtTokenDto
 * Author      : 조 준 희
 * History     : [2022-04-13] - 조 준희 - Class Create
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenDto {

    private String accessToken;
    private String refreshToken;

    @Override
    public String toString() {
        return "JwtTokenDto{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
