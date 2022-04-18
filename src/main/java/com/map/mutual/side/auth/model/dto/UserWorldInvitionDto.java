package com.map.mutual.side.auth.model.dto;

import com.map.mutual.side.common.config.BeanConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWorldInvitionDto {
    @Size(min = 18, max = 18)
    private String targetSuid;

    @Pattern(regexp = BeanConfig.phoneRegexp)
    private String phone;

    @NotNull
    private Long worldId;


}
