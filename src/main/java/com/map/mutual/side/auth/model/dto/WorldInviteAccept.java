package com.map.mutual.side.auth.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.*;

/**
 * Description : 월드 초대 응답하기.
 * Name        :
 * Author      : 조 준 희
 * History     : [2022/04/17] - 조 준 희 - Create
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorldInviteAccept {
    //초대장 고유 번호.
    @NotNull
    @Min(value = 1)
    private Long inviteNumber;

    @NotBlank
    private String userSuid;

    @NotBlank
    @Size(min = 6, max = 6)
    private String worldUserCode;

    @Pattern(regexp = "Y|N")
    private String isAccept = "N";

    public void suidChange(String decodingSuid){
        userSuid = decodingSuid;
    }

    @Override
    public String toString() {
        return "WorldInviteAccept{" +
                "inviteNumber=" + inviteNumber +
                ", userSuid='" + userSuid + '\'' +
                ", worldUserCode='" + worldUserCode + '\'' +
                ", isAccept='" + isAccept + '\'' +
                '}';
    }
}
