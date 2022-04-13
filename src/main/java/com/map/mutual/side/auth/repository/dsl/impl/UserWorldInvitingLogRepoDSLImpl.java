package com.map.mutual.side.auth.repository.dsl.impl;

import com.map.mutual.side.auth.model.dto.notification.InvitedNotiDto;
import com.map.mutual.side.auth.model.dto.notification.QInvitedNotiDto;
import com.map.mutual.side.auth.model.dto.notification.extend.notificationDto;
import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.auth.model.entity.QUserWorldInvitingLogEntity;
import com.map.mutual.side.auth.repository.dsl.UserWorldInvitingLogRepoDSL;
import com.map.mutual.side.world.model.entity.QWorldEntity;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
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
public class UserWorldInvitingLogRepoDSLImpl implements UserWorldInvitingLogRepoDSL {

    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public UserWorldInvitingLogRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }



    /**
     * Description : 월드 초대 알림 메시지 조회
     * Name        : InvitedNotiList
     * Author      : 조 준 희
     * History     : [2022-04-13] - 조 준 희 - Create
     */
    @Override
    public List<InvitedNotiDto> InvitedNotiList(String suid) {

        // 사용자에게 월드 초대 온 메시지 조회

        QUserWorldInvitingLogEntity log = new QUserWorldInvitingLogEntity("log");

        List<InvitedNotiDto> notis = jpaQueryFactory.select( new QInvitedNotiDto(log.userSuid,
                        QUserEntity.userEntity.profileUrl,
                        QWorldEntity.worldEntity.worldName,
                        log.userSuid,
                    QWorldUserMappingEntity.worldUserMappingEntity.worldUserCode
                        ))
                .from(log)
                .leftJoin(QUserEntity.userEntity)
                .on(log.userSuid.eq(QUserEntity.userEntity.suid))
                .leftJoin(QWorldEntity.worldEntity)
                .on(log.worldId.eq(QWorldEntity.worldEntity.worldId))
                .leftJoin(QWorldUserMappingEntity.worldUserMappingEntity)
                .on(QWorldUserMappingEntity.worldUserMappingEntity.userSuid.eq(log.userSuid)
                        .and(QWorldUserMappingEntity.worldUserMappingEntity.worldId.eq(log.worldId)))
                .where(log.createTime.between(LocalDateTime.now().minusWeeks(7), LocalDateTime.now())
                        .and(log.targetSuid.eq(suid)).and(log.targetSuid.eq(suid)))
                .fetch();



        return notis;
    }
}
