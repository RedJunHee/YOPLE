package com.map.mutual.side.auth.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

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
    private String serviceTosYN;

    @NotEmpty
    private String userInfoYn;

    @NotEmpty
    private String locationInfoYn;

    @NotEmpty
    private String ageCollectionYn;

    private String marketingYn;


}
