package com.map.mutual.side.auth.model.dto;

import lombok.*;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private String suid;
    @Pattern(regexp = "(?=.*[-_A-Za-z0-9])(?=.*[^-_]).{4,20}",
            message = "ID가 올바르지 않습니다.")
    private String userId;//닉네임
    private String name;
    private String phone;
    private String profileUrl;
}
