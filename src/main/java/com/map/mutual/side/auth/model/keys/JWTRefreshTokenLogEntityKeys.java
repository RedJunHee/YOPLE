package com.map.mutual.side.auth.model.keys;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class JWTRefreshTokenLogEntityKeys implements Serializable {
    private String userSuid;
    private String refreshToken;
}
