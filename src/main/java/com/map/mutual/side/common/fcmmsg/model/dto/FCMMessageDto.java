package com.map.mutual.side.common.fcmmsg.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
/**
 * fileName       : FCMMessageDto
 * author         : kimjaejung
 * createDate     : 2022/03/20
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/20        kimjaejung       최초 생성
 *
 */
@Builder
@AllArgsConstructor
@Getter
public class FCMMessageDto {
    private boolean validate_only;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Notification notification; // 모든 mobile os를 아우를수 있는 Notification
        private String token; // 특정 device에 알림을 보내기위해 사용
        private String[] topic; //토픽 관련해서 메세지를 보냄.
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }

}
