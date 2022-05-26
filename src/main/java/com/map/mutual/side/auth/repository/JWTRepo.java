package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.model.entity.JWTRefreshTokenLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Class       : JWTRepository
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-14] - 조 준희 - Class Create
 */
public interface JWTRepo extends JpaRepository<JWTRefreshTokenLogEntity, String> {
    JWTRefreshTokenLogEntity findOneByUserSuid(String suid);
    void deleteByUserSuid(String suid);
}
