package com.map.mutual.side.auth.model.dto.notification;

import com.map.mutual.side.auth.model.dto.notification.extend.notificationDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Description : 월드에 입장 noti DTO
 * Class       : WorldEntryNotiDto
 * Author      : 조 준 희
 * History     : [2022-04-13] - 조 준희 - Class Create
 */
public class WorldEntryNotiDto extends notificationDto {

    @Builder
    public WorldEntryNotiDto(LocalDateTime notiDate, String userId, String userProfileUrl, String worldName) {
        super("B"); // B 타입 알림.
        header.SetNotiDate(notiDate);
        payload = new Payload(userId,userProfileUrl,worldName);
    }

    public LocalDateTime PushDate(){return getHeader().getPushDate();}

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
