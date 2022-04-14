package com.map.mutual.side.auth.model.dto.notification.extend;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Class       : notificationDto
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-13] - 조 준희 - Class Create
 */
@Getter
public class notificationDto {

    protected Header header ;
    protected Object payload;
    protected Object data;

    public notificationDto(String pushType) {
        header = new Header(pushType);
    }

    @Getter
    protected static class Header {
        private String pushType;
        private LocalDateTime pushDate;

        public Header(String pushType) {
            this.pushType = pushType;
        }
        public void SetNotiDate(LocalDateTime date)
        {
            pushDate = date;
        }
    }

}
