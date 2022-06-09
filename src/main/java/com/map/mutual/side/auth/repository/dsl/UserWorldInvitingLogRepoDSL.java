package com.map.mutual.side.auth.repository.dsl;

import com.map.mutual.side.auth.model.dto.notification.InvitedNotiDto;
import com.map.mutual.side.auth.model.dto.notification.extend.notificationDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Class       : UserWorldInvitingLogRepoDSL
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-13] - 조 준희 - Class Create
 */
public interface UserWorldInvitingLogRepoDSL {

    /**
     * Description : 월드 초대 알림 메시지 조회
     * Name        : InvitedNotiList
     * Author      : 조 준 희
     * History     : [2022-04-13] - 조 준 희 - Create
     */
    List<InvitedNotiDto> InvitedNotiList(String suid);
    boolean existsNewNoti(String suid, LocalDateTime searchLocalDateTime);
}
