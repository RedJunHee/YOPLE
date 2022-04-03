package com.map.mutual.side.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SMSAuthReqeustDto {
    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
    message = "휴대폰 번호가 올바르지 않습니다.")
    private String phone;
    @NotNull(message = "duid가 없습니다.")
    @NotEmpty
    private String duid;
    private String responseAuthNum;

}
