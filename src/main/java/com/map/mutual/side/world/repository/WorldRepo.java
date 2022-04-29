package com.map.mutual.side.world.repository;

import com.map.mutual.side.world.model.entity.WorldEntity;
import com.map.mutual.side.world.repository.dsl.WorldRepoDSL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorldRepo extends JpaRepository<WorldEntity, Long>, WorldRepoDSL {
    WorldEntity findByWorldId(Long worldId);
    Long countAllByWorldOwner(String worldOwner);
}
