package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.model.entity.UserTOSEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Class       : UserTOSRepo
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-06] - 조 준희 - Class Create
 */
@Repository
public interface UserTOSRepo extends JpaRepository<UserTOSEntity, String> {
    void deleteBySuid(String suid);
}
