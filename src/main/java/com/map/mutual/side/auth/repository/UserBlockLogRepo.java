package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.model.entity.UserBlockLogEntity;
import com.map.mutual.side.auth.repository.dsl.UserBlockLogRepoDSL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Class       : UserBlockLogRepo
 * Author      : 조 준 희
 * Description : 사용자 차단 로그
 * History     : [2022-04-21] - 조 준희 - Class Create
 */
@Repository
public interface UserBlockLogRepo extends JpaRepository<UserBlockLogEntity, Long> , UserBlockLogRepoDSL {
    /**
     * Description : 사용자 차단 정보가 존재하는지 여부.
     * Name        : existsByUserSuidAndBlockSuidAndIsBlocking
     * Author      : 조 준 희
     * History     : [2022/04/27] - 조 준 희 - Create
     */
    boolean existsByUserSuidAndBlockSuidAndIsBlocking(String userSuid, String blockSuid, String isBlocking);
    /**
     * Description : 사용자가 현재 차단 중인 차단 리스트 조회
     * Name        : findByUserSuidAndIsBlocking
     * Author      : 조 준 희
     * History     : [2022/04/27] - 조 준 희 - Create
     */
    List<UserBlockLogEntity> findByUserSuidAndIsBlocking(String userSuid,  String isBlocking);
}
