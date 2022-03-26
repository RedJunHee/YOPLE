/**
 * fileName       : SmsDto
 * author         : kimjaejung
 * createDate     : 2022/03/12
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/12        kimjaejung       최초 생성
 *
 */
package com.map.mutual.side.auth.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SmsDto {
    private String countryCode; // 기본 82
    private String subject; //메세지 타이틀
    private String from; //메세지 발신자
    private String type; //SMS,MMS....
    private String contentType; //default COMM
    private String content; //기본 메세지 내용
    private List<MessageInfoDto> messages; //메세지 정보
    @Getter
    @Setter
    @Builder
    public static class MessageInfoDto {
        private String to; //메세지 수신자 번호
        private String content; //개별 메세지 내용
    }

}
