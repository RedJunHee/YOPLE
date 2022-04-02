package com.map.mutual.side.world.repository;

import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.model.keys.WorldUserMappingEntityKeys;
import com.map.mutual.side.world.repository.dsl.WorldUserMappingRepoDSL;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorldUserMappingRepo extends JpaRepository<WorldUserMappingEntity, WorldUserMappingEntityKeys>, WorldUserMappingRepoDSL {
    List<WorldUserMappingEntity> findByUserSuid(String userSuid);
    Optional<WorldUserMappingEntity> findByWorldIdAndUserSuid(Long worldId, String userSuid);
}
