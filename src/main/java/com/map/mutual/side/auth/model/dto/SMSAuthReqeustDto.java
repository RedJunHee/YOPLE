package com.map.mutual.side.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SMSAuthReqeustDto {
    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
    message = "휴대폰 번호가 올바르지 않습니다.")
    private String phone;

    @NotBlank(message = "디바이스값이 널 이거나 빈값입니다.")
    private String duid;

    @Size(min = 6,max = 6)
    private String responseAuthNum;

}
