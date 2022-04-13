package com.map.mutual.side.auth.model.dto.notification;

import com.map.mutual.side.auth.model.dto.notification.extend.notificationDto;
import lombok.Getter;

/**
 * Description : 월드에 입장 noti DTO
 * Class       : WorldEntryNotiDto
 * Author      : 조 준 희
 * History     : [2022-04-13] - 조 준희 - Class Create
 */
public class WorldEntryNotiDto extends notificationDto {

    public WorldEntryNotiDto(String userId, String userProfileUrl, String worldName) {
        super("B"); // B 타입 알림.

        payload = new Payload(userId,userProfileUrl,worldName);
    }



    @Getter
    private static class Payload{
        private String userId;
        private String userProfileUrl;
        private String worldName;

        public Payload(String userId, String userProfileUrl, String worldName) {
            this.userId = userId;
            this.userProfileUrl = userProfileUrl;
            this.worldName = worldName;
        }
    }
}
