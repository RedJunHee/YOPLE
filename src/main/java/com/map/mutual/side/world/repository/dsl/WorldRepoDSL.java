package com.map.mutual.side.world.repository.dsl;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.world.model.dto.WorldDetailResponseDto;
import com.map.mutual.side.world.model.entity.WorldEntity;

/**
 * Class       : WorldRepoDSL
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-21] - 조 준희 - Class Create
 */
public interface WorldRepoDSL {
    WorldDetailResponseDto getWorldDetail(Long worldId, String requestUser);
}
