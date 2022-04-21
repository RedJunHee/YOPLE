package com.map.mutual.side.auth.repository;

import com.map.mutual.side.auth.model.entity.ReviewReportLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Class       : ReviewReportLogRepo
 * Author      : 조 준 희
 * Description : 리뷰 신고 로그
 * History     : [2022-04-21] - 조 준희 - Class Create
 */
@Repository
public interface ReviewReportLogRepo extends JpaRepository<ReviewReportLogEntity, Long> {
}
