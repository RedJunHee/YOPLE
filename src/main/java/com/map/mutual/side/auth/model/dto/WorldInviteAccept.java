package com.map.mutual.side.auth.model.dto;


import com.sun.tracing.dtrace.ArgsAttributes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
    @NotBlank
    @Size(min = 1)
    private Long inviteNumber;

    @NotBlank
    @Size(min = 18, max = 18)
    private String userSuid;

    @NotBlank
    @Size(min = 6, max = 6)
    private String worldUserCode;

    @Pattern(regexp = "Y|N")
    private String isAccept = "N";
}
