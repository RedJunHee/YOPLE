package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.repository.dsl.WorldUserMappingRepoDSL;
import com.map.mutual.side.world.model.entity.WorldEntity;
import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.model.keys.WorldUserMappingEntityKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorldUserMappingRepo extends JpaRepository<WorldUserMappingEntity, WorldUserMappingEntityKeys>, WorldUserMappingRepoDSL {
//    List<WorldUserMappingEntity> findByUserInfoEntityAndWorldEntity(UserEntity userEntity, WorldEntity worldEntity);
    List<WorldUserMappingEntity> findByUserSuidAndWorldId(String userSuid, Long worldId);
    // TODO: 2022/03/25 수정한 api확인
}
