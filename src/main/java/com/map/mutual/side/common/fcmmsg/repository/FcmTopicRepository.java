package com.map.mutual.side.common.fcmmsg.repository;

import com.map.mutual.side.common.fcmmsg.model.entity.FcmTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * fileName       : FcmTopicRepository
 * author         : kimjaejung
 * createDate     : 2022/03/29
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/29        kimjaejung       최초 생성
 *
 */
@Repository
public interface FcmTopicRepository extends JpaRepository<FcmTopicEntity, Long> {
    void deleteByFcmToken(String fcmToken);
    List<FcmTopicEntity> findAllByFcmToken(String fcmToken);
}
