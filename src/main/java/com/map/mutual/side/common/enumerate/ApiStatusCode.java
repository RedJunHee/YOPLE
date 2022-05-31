package com.map.mutual.side.common.enumerate;
import lombok.Getter;

/** Class       : ApiStatusCode (Enum)
 *  Author      : 조 준 희
 *  Description : 모든 API에서 사용될 API 처리 상태 코드관리 Enum Class
 *                  - COMMON CODE - 공용 도메인 코드로 모든 도메인에서 사용될 공통 코드 분류
 *                  - ** 추가적인 도메인 코드가 생길 경우 //도메인명 CODE 와 같이 주석 후 코드 정리할 것.
 *                  - 표준 HTTP Status Code는 피할 것.
 *                  - 표준 HTTP Status : 100,200,201,204,206
 *                                      301,302,303,304,307,308
 *                                      401,403,404,405,406,407,409,410,412,416,418,425,451
 *                                      500,501,502,503,504
 *  History     : [2022-03-11] - TEMP
 */
@Getter
public enum ApiStatusCode {

    //COMMON CODE
    NONE (0,"","",true)
    ,OK (200,"OK","성공.", false)
    ,CONTENT_NOT_FOUND (204,"Content Not Found", "컨텐츠 없음.", false)
    ,AUTH_META_NOT_MATCH(211, "Auth Meta Not Match", "인증 메타 정보 불일치.",true)
    ,USER_NOT_FOUND (214,"User Not Found", "사용자 찾을 수 없음.", false)
    ,USER_ID_OVERLAPS (215, "User ID Overlaps.", "사용자 ID 중복.",false)
    ,ALREADY_USER_BLOCKING(240, "Already User Blocking", "이미 차단된 사용자입니다.", false)
    ,ALREADY_YOPLE_USER (241,"Already YOPLE User","이미 YOPLE에 가입된 정보입니다.", true)
    ,EXCEEDED_LIMITED_COUNT(250, "Exceeded limited count", "제한된 수를 초과했습니다.", true)
    ,THIS_REVIEW_IS_BLOCK_USERS_REVIEW(301, "해당 리뷰는 차단한 유저의 리뷰입니다.", "해당 리뷰는 차단한 유저의 리뷰입니다.", true)
    ,THIS_PLACE_IN_REVIEW_IS_ALREADY_EXIST(302, "해당 장소에는 이미 리뷰가 존재합니다.", "해당 장소에는 이미 리뷰가 존재합니다.", true)
    ,PARAMETER_CHECK_FAILED (400,"Bad Request","문법상 또는 파라미터 오류가 있어서 서버가 요청사항을 처리하지 못함.", true)
    ,UNAUTHORIZED (401, "UnAuthorized", "Unauthorized, 사용자 인증 실패.", true)
    ,FORBIDDEN (403, "Forbidden", "Forbidden, 사용권한 없음.", true)
    ,WORLD_LIST_IS_NULL(421, "World list is null.", "최소 하나의 월드를 추가해야 합니다.", true)
    ,ALREADY_EMOJI_ADDED(422, "Emoji status already added", "이미 추가한 이모지입니다.", true)
    ,NOT_USABLE_EMOJI(423, "Emoji type isn`t usable", "해당 이모지는 비활성화 상태입니다.", true)
    ,ALREADY_WORLD_MEMEBER(431,"Already a member of the world.","이미 월드소속 입니다.",true)
    ,WORLD_USER_CDOE_VALID_FAILED (432, "WorldUserCode is not valid.", "월드 초대 코드 유효성 실패.", true)
    ,ALREADY_WORLD_INVITING_STATUS(433,"초대 수락 대기중.","이미 초대 수락 대기중입니다.",true)
    ,INVITE_NOT_VALID(434,"Invite Not Valid.", "초대장이 유효하지 않습니다.", true)
    ,FAIL_JOIN_WORLD(435,"월드 입장에 실패했습니다.", "월드 입장에 실패했습니다.", true)
    ,REGISTRY_FCM_TOPIC_FAIL (440, "Topic 추가를 실패했습니다.", "Topic 추가를 실패했습니다.", true)
    ,UNSUBSCRIPTION_FCM_TOPIC_FAIL (441, "Topic 제거를  실패했습니다.", "Topic 제거를 실패했습니다.", true)
    ,SEND_TO_FCM_FAILED (442, "FCM 전송을 실패했습니다.", "FCM 전송을 실패했습니다.", true)
    ,GENERATE_FAILED_TO_TOKEN (443, "FCM 갱신을 실패했습니다.", "FCM 갱신을 실패했습니다.", true)
    ,FAIL_DELETE_FCM_TOKEN (444, "FCM 토큰 제거를 실패했습니다.", "FCM 토큰 제거를 실패했습니다.", true)
    ,USER_TOS_INFO_VALID_FAILED (490, "TOS 정보 체크 실패.", "TOS 정보 체크 실패.",true)
    ,SYSTEM_ERROR(599,"System Error.", "시스템오류.",true)
    ;

    //Enum 필드
    private int code;
    private String type;
    private String message;
    private Boolean isErrorType;     // API 리턴으로 type, message 보낼지 여부 에러 타입이면 보냄.

    //Enum 생성자
    ApiStatusCode(int code, String type , String message,Boolean isErrorType) {
        this.code = code;
        this.type = type;
        this.message = message;
        this.isErrorType = isErrorType;
    }
}
