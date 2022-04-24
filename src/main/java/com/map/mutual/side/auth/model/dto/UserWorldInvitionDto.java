package com.map.mutual.side.auth.model.dto;

import com.map.mutual.side.common.config.BeanConfig;
import com.map.mutual.side.common.utils.CryptUtils;
import com.map.mutual.side.common.utils.YOPLEAssert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWorldInvitionDto {

    private String targetSuid;

    @Pattern(regexp = BeanConfig.phoneRegexp)
    private String phone;

    @NotNull
    private Long worldId;

    public void suidChange(String decodingSuid){
        targetSuid = decodingSuid;
    }

}
