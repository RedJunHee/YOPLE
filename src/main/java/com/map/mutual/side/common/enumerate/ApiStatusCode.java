package com.map.mutual.side.common.enumerate;
import lombok.Getter;

/** Class       : ApiStatusCode (Enum)
 *  Author      : 조 준 희
 *  Description : 모든 API에서 사용될 API 처리 상태 코드관리 Enum Class
 *                  - COMMON CODE - 공용 도메인 코드로 모든 도메인에서 사용될 공통 코드 분류
 *                  - ** 추가적인 도메인 코드가 생길 경우 //도메인명 CODE 와 같이 주석 후 코드 정리할 것.
 *  History     : [2022-03-11] - TEMP
 */
@Getter
public enum ApiStatusCode {

    //COMMON CODE
    NONE (0,"","",true)
    ,OK (200,"OK","성공.", false)
    ,CREATED (201,"Created","리소스 생성 완료.", false)
    ,CONTENT_NOT_FOUND (204,"Content Not Found", "컨텐츠 없음.", false)
    ,AUTH_META_NOT_MATCH(211, "Auth Meta Not Match", "인증 메타 정보 불일치.",true)
    ,USER_NOT_FOUND (214,"User Not Found", "유저 찾을 수 없음.", true)
    ,PARAMETER_CHECK_FAILED (400,"Bad Request","문법상 또는 파라미터 오류가 있어서 서버가 요청사항을 처리하지 못함.", true)
    ,UNAUTHORIZED (401, "UnAuthorized", "Unauthorized, 사용자 인증 실패.", true)
    ,FORBIDDEN (403, "Forbidden", "Forbidden, 사용권한 없음.", true)
    ,ALREADY_WORLD_MEMEBER(431,"Already a member of the world","이미 월드소속 입니다.",true)
    ,SYSTEM_ERROR(599,"System Error", "시스템오류.",true)
    ;

    //Enum 필드
    private int code;
    private String type;
    private String message;
    private Boolean isErrorType;

    //Enum 생성자
    ApiStatusCode(int code, String type , String message,Boolean isErrorType) {
        this.code = code;
        this.type = type;
        this.message = message;
        this.isErrorType = isErrorType;
    }
}
