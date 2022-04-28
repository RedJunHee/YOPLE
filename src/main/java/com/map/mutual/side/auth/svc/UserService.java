package com.map.mutual.side.auth.svc;

import com.map.mutual.side.auth.model.dto.*;
import com.map.mutual.side.auth.model.dto.block.UserBlockDto;
import com.map.mutual.side.auth.model.dto.block.UserBlockedDto;
import com.map.mutual.side.auth.model.dto.notification.NotiDto;
import com.map.mutual.side.auth.model.dto.report.ReviewReportDto;
import com.map.mutual.side.auth.model.dto.report.UserReportDto;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.world.model.dto.WorldDto;

import java.util.List;

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
    UserInfoDto findUser(String id, String phone, String suid) ;
    List<UserInWorld> worldUsers(long worldId, String suid) throws Exception;

    //2. 월드에 참여하기.
    WorldDto JoinWorld( String worldinvitationCode);
    UserInfoDto userDetails(String suid);
    UserInfoDto userInfoUpdate(String suid, String userId, String profileUrl);
    void userLogout(String suid);
    UserInfoDto signUp(UserInfoDto user) throws Exception;

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
    WorldDto inviteJoinWorld(WorldInviteAccept invited, String suid);

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
    void block(String suid, UserBlockDto userBlockDto);
    /**
     * Description : 사용자 차단해지하기.
     * - 없는 차단 이력 요청할 경우 FORBIDDEN
     * - 사용자 차단 이력 아닌 경우 FORBIDDEN
     * Name        : blockCancel
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    void blockCancel(String suid, Long blockId);

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
