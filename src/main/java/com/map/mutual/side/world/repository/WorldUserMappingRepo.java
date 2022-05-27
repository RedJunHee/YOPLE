package com.map.mutual.side.world.repository;

import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.model.keys.WorldUserMappingEntityKeys;
import com.map.mutual.side.world.repository.dsl.WorldUserMappingRepoDSL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorldUserMappingRepo extends JpaRepository<WorldUserMappingEntity, WorldUserMappingEntityKeys>, WorldUserMappingRepoDSL {
    List<WorldUserMappingEntity> findByUserSuid(String userSuid);
    Optional<WorldUserMappingEntity> findByWorldIdAndUserSuid(Long worldId, String userSuid);
    Optional<WorldUserMappingEntity> findOneByWorldIdAndUserSuid(Long worldId, String userSuid);
    Optional<WorldUserMappingEntity> findOneByWorldUserCode(String worldUserCode);
    Optional<WorldUserMappingEntity> findTop1ByUserSuidOrderByAccessTimeDesc(String userSuid);
    void deleteByUserSuid(String suid);

    @Query("SELECT COUNT(e.userSuid) FROM WorldUserMappingEntity e WHERE e.userSuid = :suid AND e.worldUserCode <> e.worldinvitationCode")
    Long countAllByActiveWorlds(@Param(value = "suid") String userSuid);
}
