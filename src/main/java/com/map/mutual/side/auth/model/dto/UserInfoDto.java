package com.map.mutual.side.auth.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {
    private String suid;

    @Pattern(regexp = "(?=.*[-_A-Za-z0-9])(?=.*[^-_]).{4,20}",
            message = "ID가 올바르지 않습니다.")
    private String userId;//닉네임

    // TODO: 2022/04/10 사용자 이름 벨리데이션 추가 필요.
    private String name;

    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
            message = "핸드폰 번호가 올바르지 않습니다.")
    private String phone;

    // TODO: 2022/04/10 프로필 사진 경로 패턴 벨리데이션 추가 필요.
    private String profileUrl;

    private String profilePinUrl;

    @JsonIgnore
    private LocalDateTime notiCheckDt;

    @JsonProperty(value = "tos")
    @Valid
    private UserTOSDto userTOSDto;

    @Override
    public String toString() {
        return "UserInfoDto{" +
                "suid='" + suid + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", profilePinUrl='" + profilePinUrl + '\'' +
                ", notiCheckDt=" + notiCheckDt +
                ", userTOSDto=" + userTOSDto +
                '}';
    }
}
