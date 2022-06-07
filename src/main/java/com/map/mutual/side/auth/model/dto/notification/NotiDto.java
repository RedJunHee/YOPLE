package com.map.mutual.side.auth.model.dto.notification;

import com.map.mutual.side.auth.model.dto.notification.extend.notificationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Class       : notiDTO
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-14] - 조 준희 - Class Create
 */

@Getter
@NoArgsConstructor
public class NotiDto {

    private List<notificationDto> topNoti;
    private List<notificationDto> middleNoti;

    @Builder
    public NotiDto(List<notificationDto> topNoti, List<notificationDto> middleNoti) {
        this.topNoti = topNoti;
        this.middleNoti = middleNoti;
    }

}
