package com.map.mutual.side.auth.repository.dsl.impl;

import com.map.mutual.side.auth.model.dto.block.QUserBlockedDto;
import com.map.mutual.side.auth.model.dto.block.UserBlockedDto;
import com.map.mutual.side.auth.model.dto.notification.InvitedNotiDto;
import com.map.mutual.side.auth.model.dto.notification.QInvitedNotiDto;
import com.map.mutual.side.auth.model.entity.QUserBlockLogEntity;
import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.auth.model.entity.QUserWorldInvitingLogEntity;
import com.map.mutual.side.auth.model.entity.UserBlockLogEntity;
import com.map.mutual.side.auth.repository.dsl.UserBlockLogRepoDSL;
import com.map.mutual.side.auth.repository.dsl.UserWorldInvitingLogRepoDSL;
import com.map.mutual.side.world.model.entity.QWorldEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Class       : UserWorldInvitingLogRepoDSLImpl
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-13] - 조 준희 - Class Create
 */
public class UserBlockLogRepoDSLImpl implements UserBlockLogRepoDSL {

    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public UserBlockLogRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }



    /**
     * Description : 월드 초대 알림 메시지 조회
     * Name        : InvitedNotiList
     * Author      : 조 준 희
     * History     : [2022-04-13] - 조 준 희 - Create
     */
    @Override
    public List<UserBlockedDto> findBlockList(String suid) {

        // 사용자에게 월드 초대 온 메시지 조회

        QUserBlockLogEntity log = new QUserBlockLogEntity("log");

        List<UserBlockedDto> users = jpaQueryFactory.select( new QUserBlockedDto(
                    log.seq,
                QUserEntity.userEntity.userId,
                QUserEntity.userEntity.name,
                QUserEntity.userEntity.profileUrl
                ) )
                .from(log)
                .innerJoin(QUserEntity.userEntity)
                .on(log.blockSuid.eq(QUserEntity.userEntity.suid))
                .where(log.userSuid.eq(suid).and( log.isBlocking.eq("Y")))
                .orderBy(log.createTime.desc())
                .fetch();

        return users;
    }
}
