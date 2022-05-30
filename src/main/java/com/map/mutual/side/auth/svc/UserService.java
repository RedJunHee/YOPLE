package com.map.mutual.side.auth.svc;

import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.dto.WorldInviteAccept;
import com.map.mutual.side.auth.model.dto.block.UserBlockDto;
import com.map.mutual.side.auth.model.dto.block.UserBlockedDto;
import com.map.mutual.side.auth.model.dto.notification.NotiDto;
import com.map.mutual.side.auth.model.dto.report.ReviewReportDto;
import com.map.mutual.side.auth.model.dto.report.UserReportDto;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.world.model.dto.WorldDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * fileName       : UserService
 * author         : kimjaejung
 * createDate     : 2022/03/16
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/16        kimjaejung       최초 생성
 *
 */
public interface UserService {
    UserInfoDto findUser(String id, String phone, String suid) throws YOPLEServiceException;
    List<UserInWorld> worldUsers(long worldId, String suid) throws Exception;

    //2. 월드에 참여하기.
    WorldDto JoinWorld( String worldinvitationCode) throws YOPLEServiceException, ExecutionException, InterruptedException;
    UserInfoDto userDetails(String suid) throws YOPLEServiceException;
    UserInfoDto userInfoUpdate(String suid, String userId, String profileUrl, String profilePinUrl) throws YOPLEServiceException;
    void userLogout(String suid);
    void userWithdrawal() throws YOPLEServiceException;
    UserInfoDto signUp(UserInfoDto user) throws Exception;

    /**
     * Description :  알림 리스트 조회시간 갱신
     * 독바의 최근 알림마커는 USER_INFO테이블의 NOTI_CHECK_DT기준으로 최신 알림건 있는지 알아옴.
     * NOTI_CHECK_DT가 갱신되는 기준은 알림 리스트 페이지를 들어갔을때임.
     * 알림 리스트 조회에서 호출되는 함수.
     * Name        : notiCheckDtUpdate
     * Author      : 조 준 희
     * History     : [2022/05/30] - 조 준 희 - Create
     */
    void notiCheckDtUpdate(String suid) throws YOPLEServiceException;

    /**
     * Description :  독바 최신 알림 여부 마커 조회 (유저 디테일)
     * 독바의 최근 알림마커 최신 알림 여부 확인.
     * Name        : newNotiCheck
     * Author      : 조 준 희
     * History     : [2022/05/30] - 조 준 희 - Create
     */
    boolean newNotiCheck(String suid) throws YOPLEServiceException;

    /**
     * Description :  사용자 월드 초대하기.
     *                  - 초대자가 월드에 참여중이 아닌경우 FORBIDDEN Exception
     *                  - 초대받는자가 월드에 참여인경우 ALREADY_WORLD_MEMEBER Exception
     *                  - 초대 수락 대기 중인 경우 ALREADY_WORLD_INVITING_STATUS
     * Name        : userWorldInviting
     * Author      : 조 준 희
     * History     : [2022/04/17] - 조 준 희 - Create
     */
    void userWorldInviting(String suid, String targetSuid, Long worldId)  throws YOPLEServiceException;
    /**
     * Description : 미 가입 사용자 YOPLE 월드 초대하기 문자.
     * Name        : unSignedUserWorldInviting
     * Author      : 조 준 희
     * History     : [2022/04/17] - 조 준 희 - Create
     */
    void unSignedUserWorldInviting(String suid, String targetPhone, Long worldId) throws YOPLEServiceException;

    /**
     * Description : 최근 접속한 월드 ID 조회하기.( Default = 0L )
     * Name        : getRecentAccessWorldID
     * Author      : 조 준 희
     * History     : [2022/04/17] - 조 준 희 - Create
     */
    Long getRecentAccessWorldID(String suid);

    /**
     * Description : 알림 리스트 조회하기.
     * Name        : notificationList
     * Author      : 조 준 희
     * History     : [2022/04/17] - 조 준 희 - Create
     */
    NotiDto notificationList(String suid) throws Exception;

    /**
     * Description : 월드 초대에 응답하기. 수락하기, 거절하기.
     * Name        : inviteJoinWorld
     * Author      : 조 준 희
     * History     : [2022/04/17] - 조 준 희 - Create
     */
    WorldDto inviteJoinWorld(WorldInviteAccept invited, String suid) throws YOPLEServiceException, ExecutionException, InterruptedException;

    /**
     * Description : 사용자 신고하기.
     * Name        : report
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    void report(String suid, UserReportDto userReportDto);

    /**
     * Description : 사용자 차단하기.
     * - 이미 차단된 유저인경우 ALREADY_USER_BLOCKING
     * Name        : block
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    void block(String suid, UserBlockDto userBlockDto) throws YOPLEServiceException;
    /**
     * Description : 사용자 차단해지하기.
     * - 없는 차단 이력 요청할 경우 FORBIDDEN
     * - 사용자 차단 이력 아닌 경우 FORBIDDEN
     * Name        : blockCancel
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    void blockCancel(String suid, Long blockId) throws YOPLEServiceException;

    /**
     * Description : 사용자 차단리스트 조회
     * Name        : getBlock
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    List<UserBlockedDto> getBlock(String suid);


    /**
     * Description : 리뷰 신고하기.
     * Name        : reviewReport
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    void reviewReport(String suid, ReviewReportDto reviewReportDto);
}
