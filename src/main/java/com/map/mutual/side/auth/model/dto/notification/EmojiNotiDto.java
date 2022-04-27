package com.map.mutual.side.auth.model.dto.notification;

import com.map.mutual.side.auth.model.dto.notification.extend.notificationDto;
import com.map.mutual.side.common.utils.CryptUtils;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Class       : EmojiNotiDto
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-13] - 조 준희 - Class Create
 */
public class EmojiNotiDto extends notificationDto {

    @Builder
    public EmojiNotiDto(LocalDateTime notiDate, String userId, String userProfileUrl, String worldName, Long reviewId, Long worldId, String placeId, BigDecimal x,BigDecimal y) {
        super("C"); // C 타입 알림.
        header.SetNotiDate(notiDate);
        payload = new Payload(userId,userProfileUrl,worldName);
        data = new Data(reviewId, worldId, placeId,x,y);
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

    @Getter
    private static class Data{
        private Long reviewId;
        private Long worldId;
        private String placeId;
        private BigDecimal x;
        private BigDecimal y;

        public Data(Long reviewId, Long worldId, String placeId, BigDecimal x, BigDecimal y) {
            this.reviewId = reviewId;
            this.worldId = worldId;
            this.placeId = placeId;
            this.x = x;
            this.y = y;
        }
    }

}
