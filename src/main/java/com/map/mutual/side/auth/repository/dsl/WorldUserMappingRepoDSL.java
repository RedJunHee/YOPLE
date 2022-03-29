package com.map.mutual.side.auth.repository.dsl;

import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.world.model.dto.WorldDto;

import java.util.List;

public interface WorldUserMappingRepoDSL {

    List<WorldDto> findBySuidWithWorld(String suid);
    List<UserInWorld> findAllUsersInWorld(long worldId);
    Long exsistUserInWorld(String worldinvitationCode, String suid);
}
