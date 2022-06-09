package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepo extends JpaRepository<UserEntity, String> {
    UserEntity findOneByPhone(String phone);
    UserEntity findByUserId(String userid);
    UserEntity findBySuid(String suid);
    void deleteBySuid(String suid);
}
