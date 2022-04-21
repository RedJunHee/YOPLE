package com.map.mutual.side.auth.repository.dsl;

import com.map.mutual.side.auth.model.dto.block.UserBlockedDto;
import com.map.mutual.side.auth.model.dto.notification.InvitedNotiDto;
import com.map.mutual.side.auth.model.entity.UserBlockLogEntity;

import java.util.List;

/**
 * Class       : UserWorldInvitingLogRepoDSL
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-13] - 조 준희 - Class Create
 */
public interface UserBlockLogRepoDSL {

    /**
     * Description : 월드 초대 알림 메시지 조회
     * Name        : InvitedNotiList
     * Author      : 조 준 희
     * History     : [2022-04-13] - 조 준 희 - Create
     */
    List<UserBlockedDto> findBlockList(String suid);
}
