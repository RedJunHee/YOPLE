package com.map.mutual.side.auth.repository.dsl;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.world.model.dto.WorldDto;

import java.util.List;

public interface WorldUserMappingRepoDSL {

    List<WorldDto> findBySuidWithWorld(String suid);
    List<UserEntity> findAllUsersInWorldCode(long worldId);
}
