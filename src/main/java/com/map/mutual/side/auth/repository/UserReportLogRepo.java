package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.model.entity.UserReportLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Class       : UserReportLogRepo
 * Author      : 조 준 희
 * Description : 사용자 신고 로그
 * History     : [2022-04-21] - 조 준희 - Class Create
 */
@Repository
public interface UserReportLogRepo extends JpaRepository<UserReportLogEntity, Long> {
}
