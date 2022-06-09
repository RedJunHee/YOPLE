package com.map.mutual.side.auth.repository.dsl.impl;

import com.map.mutual.side.auth.model.dto.notification.InvitedNotiDto;
import com.map.mutual.side.auth.model.dto.notification.QInvitedNotiDto;
import com.map.mutual.side.auth.model.dto.notification.extend.notificationDto;
import com.map.mutual.side.auth.model.entity.QUserEntity;
import com.map.mutual.side.auth.model.entity.QUserWorldInvitingLogEntity;
import com.map.mutual.side.auth.repository.dsl.UserWorldInvitingLogRepoDSL;
import com.map.mutual.side.world.model.entity.QWorldEntity;
import com.map.mutual.side.world.model.entity.QWorldUserMappingEntity;
import com.querydsl.core.types.dsl.CaseBuilder;
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
     * Description : 월드 초대 알림 최신건 있는지 여부.
     * Name        : existsNewNoti
     * Author      : 조 준 희
     * History     : [2022/05/30] - 조 준 희 - Create
     */
    @Override
    public boolean existsNewNoti(String suid, LocalDateTime searchLocalDateTime) {
        QUserWorldInvitingLogEntity log = new QUserWorldInvitingLogEntity("log");

        boolean existsYN = false;

        // 존재 한다면 True 존재하지 않다면 False
        existsYN = jpaQueryFactory.select (log.userSuid)
                        .from(log)
                        .where(log.userSuid.eq(suid)
                               .and(log.createTime.after(searchLocalDateTime))
                                .and(log.invitingStatus.eq("-")))
                        .fetchFirst() != null ;

        return existsYN;
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

        List<InvitedNotiDto> notis = jpaQueryFactory.select( new QInvitedNotiDto(log.createTime,
                        QUserEntity.userEntity.userId,
                        QUserEntity.userEntity.name,
                        QUserEntity.userEntity.profileUrl, // 프로필 사진은 Optional Column
                        QWorldEntity.worldEntity.worldName,
                        log.seq,
                        log.userSuid,
                        log.worldUserCode,
                        QWorldEntity.worldEntity.worldId
                        ))
                .from(log)
                .innerJoin(QUserEntity.userEntity)
                .on(log.userSuid.eq(QUserEntity.userEntity.suid))
                .innerJoin(QWorldEntity.worldEntity)
                .on(log.worldId.eq(QWorldEntity.worldEntity.worldId))
                .where(log.createTime.between(LocalDateTime.now().minusWeeks(12), LocalDateTime.now())
                        .and(log.targetSuid.eq(suid)).and(log.targetSuid.eq(suid)).and(log.invitingStatus.eq("-")))
                .orderBy(log.createTime.desc())
                .fetch();



        return notis;
    }
}
