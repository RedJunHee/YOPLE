package com.map.mutual.side.auth.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

/**
 * Class       : UserTOSEntity
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-06] - 조 준희 - Class Create
 */
@Getter
@NoArgsConstructor
public class UserTOSDto {

    @NotEmpty
    @Pattern(regexp = "Y|N")
    private String serviceTosYN;

    @NotEmpty
    @Pattern(regexp = "Y|N")
    private String userInfoYN;

    @NotEmpty
    @Pattern(regexp = "Y|N")
    private String locationInfoYN;

    @NotEmpty
    @Pattern(regexp = "Y|N")
    private String ageCollectionYN;

    @Pattern(regexp = "Y|N")
    private String marketingYN="N";


}
