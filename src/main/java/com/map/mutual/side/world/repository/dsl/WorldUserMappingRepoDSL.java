package com.map.mutual.side.world.repository.dsl;

import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.world.model.dto.WorldDto;

import java.util.List;

public interface WorldUserMappingRepoDSL {

    List<WorldDto> findBySuidWithWorld(String suid);
    List<WorldDto> findBySuidWithWorldDetails (String suid);

    //월드에 참여중인 사용자 조회
    List<UserInWorld> findAllUsersInWorld(long worldId, String suid);

    Long exsistUserCodeInWorld(String worldinvitationCode, String suid);
    Boolean exsistUserInWorld(Long worldId, String suid);
}
