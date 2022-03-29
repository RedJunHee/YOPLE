package com.map.mutual.side.auth.model.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInWorld {
    private String suid;
    private String userId;
    private String name;
    private String profileUrl;
    private String invitee;


    @Builder
    @QueryProjection
    public UserInWorld(String suid, String userId, String name, String profileUrl, String invitee) {
        this.suid = suid;
        this.userId = userId;
        this.name = name;
        this.profileUrl = profileUrl;
        this.invitee = invitee;
    }
}
