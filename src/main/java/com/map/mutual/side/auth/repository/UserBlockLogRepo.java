package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.model.entity.UserBlockLogEntity;
import com.map.mutual.side.auth.repository.dsl.UserBlockLogRepoDSL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Class       : UserBlockLogRepo
 * Author      : 조 준 희
 * Description : 사용자 차단 로그
 * History     : [2022-04-21] - 조 준희 - Class Create
 */
@Repository
public interface UserBlockLogRepo extends JpaRepository<UserBlockLogEntity, Long> , UserBlockLogRepoDSL {
    boolean existsByUserSuidAndBlockSuidAndAndIsBlocking(String userSuid, String blockSuid, String isBlocking);
}
