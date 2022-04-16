package com.map.mutual.side.common.fcmmsg.constant;

import lombok.Getter;

public class FCMConstant {
    public static final String EXPIRED = "EXPIRED";




    //configuration constant
    public static final String FCM_INSTANCE = "FCM";
    public static final String YOPLE = "YOPLE";

    //fcm msg Type
    @Getter
    public enum MSGType {
        A('A', "내가 다른 월드에 초대된 경우"),
        B('B', "월드에 신규 멤버가 초대된 경우"),
        C('C', "리뷰에 이모지를 남긴 경우")
        ;

        private char type;
        private String desc;

        MSGType(char type, String desc) {
            this.type = type;
            this.desc = desc;
        }
    }

    //fcm msg result type
    @Getter
    public enum ResultType {
        SUCCESS("SUCCESS", "[FCM]알림 전송을 성공했습니다."),
        FAIL("FAIL", "[FCM]알림 전송을 실패했습니다."),
        ;

        private String type;
        private String desc;

        ResultType(String type, String desc) {
            this.type = type;
            this.desc = desc;
        }
    }

}
