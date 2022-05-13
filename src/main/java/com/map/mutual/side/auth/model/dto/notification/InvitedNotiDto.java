package com.map.mutual.side.auth.model.dto.notification;

import com.map.mutual.side.auth.model.dto.notification.extend.notificationDto;
import com.map.mutual.side.common.utils.CryptUtils;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Class       : InvitationNotiDto
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-13] - 조 준희 - Class Create
 */
public class InvitedNotiDto extends notificationDto {

    @QueryProjection
    public InvitedNotiDto(LocalDateTime notiDate,  String userId,String userName , String userProfileUrl, String worldName, Long inviteNumber, String userSuid, String worldUserCode ) {
        super("A"); // A 타입 알림.
        header.SetNotiDate(notiDate);
        payload = new Payload(userId,userName,userProfileUrl,worldName);
        data = new Data(inviteNumber,userSuid,worldUserCode);
    }
    public LocalDateTime PushDate(){return getHeader().getPushDate();}
    public void decodingSuid() throws Exception {
        ((Data)data).userSuid = CryptUtils.AES_Encode(((Data)data).userSuid);
    }

    @Getter
    private static class Payload{
        private String userId;
        private String userName;
        private String userProfileUrl;
        private String worldName;

        public Payload(String userId,String userName, String userProfileUrl, String worldName) {
            this.userId = userId;
            this.userName = userName;
            this.userProfileUrl = userProfileUrl;
            this.worldName = worldName;
        }
    }

    @Getter
    private static class Data{
        private Long inviteNumber;
        private String userSuid;
        private String worldUserCode;



        public Data(Long inviteNumber, String userSuid, String worldUserCode) {
            this.inviteNumber = inviteNumber;
            this.userSuid = userSuid;
            this.worldUserCode = worldUserCode;
        }
    }

}
