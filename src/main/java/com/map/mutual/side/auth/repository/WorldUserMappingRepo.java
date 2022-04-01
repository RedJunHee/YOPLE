package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.repository.dsl.WorldUserMappingRepoDSL;
import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.model.keys.WorldUserMappingEntityKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorldUserMappingRepo extends JpaRepository<WorldUserMappingEntity, WorldUserMappingEntityKeys>, WorldUserMappingRepoDSL {
    Optional<WorldUserMappingEntity> findByWorldIdAndAndUserSuid(Long worldId, String userSuid);
    List<WorldUserMappingEntity> findByUserSuid(String userSuid);
}
