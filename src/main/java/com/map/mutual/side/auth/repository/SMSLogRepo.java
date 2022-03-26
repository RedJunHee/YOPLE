package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.model.entity.SMSRequestLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SMSLogRepo extends JpaRepository<SMSRequestLogEntity, Long> {
    SMSRequestLogEntity findTop1ByPhoneAndCreateTimeBetweenOrderByCreateTime(String phone, LocalDateTime start, LocalDateTime end);
}
