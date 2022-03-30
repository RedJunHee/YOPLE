package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.model.entity.UserWorldInvitingLogEntity;
import com.map.mutual.side.auth.model.keys.UserWorldInvitingLogKeys;
import com.map.mutual.side.auth.repository.dsl.WorldUserMappingRepoDSL;
import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.model.keys.WorldUserMappingEntityKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserWorldInvitingLogRepo extends JpaRepository<UserWorldInvitingLogEntity, UserWorldInvitingLogKeys> {
}
