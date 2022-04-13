package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.model.entity.UserWorldInvitingLogEntity;
import com.map.mutual.side.auth.model.keys.UserWorldInvitingLogKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserWorldInvitingLogRepo extends JpaRepository<UserWorldInvitingLogEntity, UserWorldInvitingLogKeys> {
    Optional<UserWorldInvitingLogEntity> findOneByUserSuidAndTargetSuidAndWorldIdAndInvitingStatus(String userSuid, String targetSuid, Long worldId, String invitingStatus);

}
