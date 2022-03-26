package com.map.mutual.side.common.repository;

import com.map.mutual.side.common.entity.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Class       : LogRepository (Repository)
 *  Author      : 조 준 희
 *  Description : 로그 DB 커넥션 객체  ( Interface의 필요성 아직 모르겠어서 없음. )
 *  History     : [2022-01-03] - Temp
 */
@Repository
public interface LogRepository extends JpaRepository<ApiLog,Long> {

}
