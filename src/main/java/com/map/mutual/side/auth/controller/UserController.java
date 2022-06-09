package com.map.mutual.side.auth.controller;


import com.map.mutual.side.auth.model.dto.*;
import com.map.mutual.side.auth.model.dto.block.UserBlockDto;
import com.map.mutual.side.auth.model.dto.block.UserBlockedDto;
import com.map.mutual.side.auth.model.dto.notification.NotiDto;
import com.map.mutual.side.auth.model.dto.report.ReviewReportDto;
import com.map.mutual.side.auth.model.dto.report.UserReportDto;
import com.map.mutual.side.auth.model.entity.JWTRefreshTokenLogEntity;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.auth.svc.AuthService;
import com.map.mutual.side.auth.svc.UserService;
import com.map.mutual.side.common.config.BeanConfig;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.fcmmsg.constant.FCMConstant;
import com.map.mutual.side.common.fcmmsg.svc.FCMService;
import com.map.mutual.side.common.utils.CryptUtils;
import com.map.mutual.side.world.model.dto.WorldDto;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * fileName       : UserController
 * author         : kimjaejung
 * createDate     : 2022/03/16
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/16        kimjaejung       최초 생성
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    private final Logger logger = LogManager.getLogger(UserController.class);
    private AuthService authService;
    private UserService userService;
    private UserInfoRepo userInfoRepo;
    private FCMService fcmService;

    @Autowired
    public UserController(AuthService authService, UserService userService, UserInfoRepo userInfoRepo, FCMService fcmService) {
        this.authService = authService;
        this.userService = userService;
        this.userInfoRepo = userInfoRepo;
        this.fcmService = fcmService;
    }

    /**
     * Description : 1. 회원가입.
     * Name        : smsSignUp
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<ResponseJsonObject> smsSignUp(@RequestBody @Valid UserInfoDto userInfoDto) throws Exception {
        try {

            if (userInfoDto.getUserTOSDto().getUserInfoYN().equals("Y") == false
                    || userInfoDto.getUserTOSDto().getLocationInfoYN().equals("Y") == false
                    || userInfoDto.getUserTOSDto().getAgeCollectionYN().equals("Y") == false
                    || userInfoDto.getUserTOSDto().getServiceTosYN().equals("Y") == false
            ) {
                logger.error("회원가입 ERROR : TOS 정보 체크 실패.");
                throw new YOPLEServiceException(ApiStatusCode.USER_TOS_INFO_VALID_FAILED);
            }

            if (StringUtil.isNullOrEmpty(userInfoDto.getProfileUrl()) == false && StringUtil.isNullOrEmpty(userInfoDto.getProfilePinUrl()) == true) {
                logger.error("회원가입 ERROR : 핀 이미지 없음.");
                throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED,"pin Image is null");
            }

            String suid = "YO";
            LocalDate date = LocalDate.now();

            suid += String.format("%02d", date.getYear())
                    + String.format("%02d", date.getMonthValue())
                    + String.format("%02d", date.getDayOfMonth())
                    + userInfoDto.getPhone().substring(3);


            userInfoDto.setSuid(suid);

            // 회원 가입 된 유저의 정보 반환
            UserInfoDto user = userService.signUp(userInfoDto);
            logger.debug("회원가입 : 회원가입 된 유저 ( {} )",user.toString());

            //JWT 발급.
            String accessJwt = authService.makeAccessJWT(user);
            String refreshJwt = authService.makeRefreshJWT(user.getSuid());

            JWTRefreshTokenLogEntity log = JWTRefreshTokenLogEntity
                    .builder().refreshToken(refreshJwt).userSuid(user.getSuid())
                    .build();

            authService.saveJwtLog(log);

            JwtTokenDto jwtTokenDto = JwtTokenDto.builder().accessToken(accessJwt).refreshToken(refreshJwt).build();

            logger.debug("회원가입 : JWT 발급 완료. { {} }",jwtTokenDto.toString());

            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(jwtTokenDto), HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("사용자 회원가입 Exception : {}" , e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("사용자 회원가입 ERROR : {}" , e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 2. 로그아웃
     * - 리프레시 토큰 삭제처리
     * Name        :  userLogout
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @DeleteMapping("/user")
    public ResponseEntity<ResponseJsonObject> userLogout() throws YOPLEServiceException {

        ResponseJsonObject responseJsonObject;

        try {
            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            userService.userLogout(userInfoDto.getSuid());

            //2. fcm 유저 토큰 / 토픽 제거
            fcmService.deleteFcmToken(userInfoDto.getSuid());

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("사용자 로그아웃 Exception : {}" , e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("사용자 로그아웃 ERROR : {}" , e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 3. 사용자 상세정보 조회
     * Name        : userDetails
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping("/user")
    public ResponseEntity<ResponseJsonObject> userDetails() throws YOPLEServiceException {
        ResponseJsonObject responseJsonObject;

        try {
            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto) authentication.getPrincipal();

            // 2. 사용자 상세정보 조회
            UserInfoDto userDetails = userService.userDetails(userToken.getSuid());
            logger.debug("사용자 상세정보 조회 : 사용자 정보 ( {} )", userDetails.toString());

            // 3. 사용자 최근 접속 월드 ID 조회
            Long recentAccessWorldID = userService.getRecentAccessWorldID(userToken.getSuid());
            logger.debug("사용자 상세정보 조회 :  최근 접속 월드 ID : {} ", recentAccessWorldID);

            // 4. 응답 생성.
            Map<String, Object> responseMap = new HashMap<>();

            responseMap.put("recentWorldId", recentAccessWorldID);
            responseMap.put("user", userDetails);

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            responseJsonObject.setData(responseMap);


            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("사용자 상세정보 조회 Exception : {}" , e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("사용자 상세정보 조회 ERROR : {}" , e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 4. 사용자 프로필 수정.
     * - 사용자 ID 이미 사용중이면 USER_ID_OVERLAPS 예외.
     * Name        :  userInfoUpdate
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PatchMapping("/user")
    public ResponseEntity<ResponseJsonObject> userInfoUpdate(@RequestParam(required = false) @Valid @Pattern(regexp = BeanConfig.userIdRegexp,
            message = "ID가 올바르지 않습니다.") String userId,
                                                             @RequestParam(required = false) String profileUrl,
                                                             @RequestParam(required = false) String profilePinUrl) throws YOPLEServiceException {

        ResponseJsonObject responseJsonObject;

        try {

            // 1.  파라미터 체크 에러.  - 프로필 사진이 들어온 경우 핀 프로필도 들어와야함.
            if (StringUtil.isNullOrEmpty(profileUrl) == false)
                if (StringUtil.isNullOrEmpty(profilePinUrl) == true){
                    logger.error("사용자 프로필 수정 : 프로필 핀 이미지 경로가 널이거나 빈 값.");
                    throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED,"pin 이미지가 널이거나 빈 값 입니다.");
                }

            // 1. 파라미터 체크 에러    - ID나 프로필 사진 둘 중 하나는 들어와야함.
            if (StringUtil.isNullOrEmpty(userId) && StringUtil.isNullOrEmpty(profileUrl)) {
                logger.error("사용자 프로필 수정 : ID 또는 프로필 사진 중 1가지는 INPUT 들어와야 함.");
                throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED, "userId 또는 profileUrl 둘 중 하나는 입력되어야합니다.");
            }

            // 2. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto) authentication.getPrincipal();

            UserInfoDto updatedUser = userService.userInfoUpdate(userToken.getSuid(), userId, profileUrl, profilePinUrl);

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(updatedUser);

            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("사용자 프로필 수정 Exception : {}" , e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("사용자 프로필 수정 ERROR : {}" , e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 5. 사용자 찾기.
     * Name        : findUserByIdOrPhone
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping("/find-user")
    public ResponseEntity<ResponseJsonObject> findUserByIdOrPhone(@RequestParam(required = false) @Valid @Pattern(regexp = BeanConfig.userIdRegexp,
            message = "ID가 올바르지 않습니다.") String userId,
                                                                  @RequestParam(required = false) @Valid @Pattern(regexp = BeanConfig.phoneRegexp,
                                                                          message = "핸드폰 번호가 올바르지 않습니다.") String phone) throws Exception {
        ResponseJsonObject response;
        try {

            // 1. 사용자 SUID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto requestUser = (UserInfoDto) authentication.getPrincipal();

            // 2. 둘 중에 하나도 안들어오면 파라미터 체크 에러.
            if (StringUtil.isNullOrEmpty(userId) && StringUtil.isNullOrEmpty(phone))
                throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED, "사용자 ID 또는 핸드폰 번호 중에 한 정보 이상 요청해야합니다.");

            UserInfoDto userInfoDto;

            userInfoDto = userService.findUser(userId, phone, requestUser.getSuid());
            logger.debug("사용자 찾기 : 찾은 사용자 ( {} )",userInfoDto.toString());

            response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            response.setData(userInfoDto);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("사용자 찾기 Exception : {}", e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("사용자 찾기 ERROR : {}" , e.getMessage());
            throw e;
        }


    }

    /**
     * Description : 6. 사용자 ID 중복체크
     * Name        : checkUserId
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping("/check-userid")
    public ResponseEntity<ResponseJsonObject> checkUserId(@RequestParam("userId") @Valid
                                                          @Pattern(regexp = BeanConfig.userIdRegexp, message = "ID가 올바르지 않습니다.") String userId) {
        try {
            ResponseJsonObject response;

            if (userInfoRepo.findByUserId(userId) == null) {
                response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            } else {
                response = ResponseJsonObject.withStatusCode(ApiStatusCode.USER_ID_OVERLAPS);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("사용자 ID 중복체크 ERROR : {}" , e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 7. 월드 참여자 리스트
     * Name        :  worldUsers
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping("/world/users")
    public ResponseEntity<ResponseJsonObject> worldUsers(@RequestParam @Valid @NotNull long worldId) throws Exception {
        ResponseJsonObject response;
        try {

            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto) authentication.getPrincipal();

            // 1. 월드에 참여 중인 사용자 조회.
            List<UserInWorld> userInfoDto = userService.worldUsers(worldId, userToken.getSuid());
            logger.debug("월드 참여자 리스트 : 월드 ID( {} )의 월드 참여자 {}명", worldId,userInfoDto.stream().count());

            // 2. 응답 객체 설정
            Map<String, Object> Users = new HashMap<>();

            Users.put("users", userInfoDto);
            response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            response.setData(Users);

            // 3. 리턴.
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("월드 참여자 리스트 Exception : {}" , e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("월드 참여자 리스트 ERROR : {}" , e.getMessage());
            throw e;
        }

    }

    /**
     * Description : 8. 월드에 사용자 초대하기.
     * - 초대자가 월드에 참여중이 아닌 경우 YOPLEServiceException(FORBIDDEN) Throw
     * - 이미 월드에 참여 중인 경우 YOPLEServiceException(ALREADY_WORLD_MEMEBER) Throw
     * - 사용자가 이미 초대 대기 중인 경우 YOPLEServiceException() Throw
     * Name        : userWorldInviting
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PostMapping("/user/world")
    public ResponseEntity<ResponseJsonObject> userWorldInviting(@RequestBody @Valid UserWorldInvitionDto userWorldInvitionDto) throws Exception {

        ResponseJsonObject responseJsonObject;

        try {
            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto) authentication.getPrincipal();

            // SUID로 초대하기 요청 온 경우. => 회원 유저임.
            if (StringUtil.isNullOrEmpty(userWorldInvitionDto.getTargetSuid()) == false) {

                // targetSuid AES256 디코딩 변환.
                userWorldInvitionDto.suidChange(CryptUtils.AES_Decode(userWorldInvitionDto.getTargetSuid()));

                userService.userWorldInviting(userToken.getSuid(), userWorldInvitionDto.getTargetSuid(), userWorldInvitionDto.getWorldId());
                logger.debug("월드에 사용자 초대하기 : 초대자({}), 수락자({}), 월드 ID({})",userToken.getSuid(), userWorldInvitionDto.getTargetSuid(), userWorldInvitionDto.getWorldId());
                String fcmToken = userInfoRepo.findBySuid(userWorldInvitionDto.getTargetSuid()).getFcmToken();

                // 알림 전송
                logger.debug("월드에 사용자 초대하기 : FCM 알림 전송 Start ");
                fcmService.sendNotificationToken(fcmToken, FCMConstant.MSGType.A, userToken.getSuid(), userWorldInvitionDto.getWorldId(), null);
                logger.debug("월드에 사용자 초대하기 : FCM 알림 전송 End ");

            } else if (StringUtil.isNullOrEmpty(userWorldInvitionDto.getPhone()) == false) {
                // TODO: 2022/04/17   유저의 핸드폰 번호 or ID 핸드폰 번호로 일단 개발진행
                userService.unSignedUserWorldInviting(userToken.getSuid(), userWorldInvitionDto.getPhone(), userWorldInvitionDto.getWorldId());
            } else {
                throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED, "userId 또는 핸드폰 번호로 초대해야합니다.");
            }

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("월드 사용자 초대하기 Exception : {}" , e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("월드 사용자 초대하기 ERROR : {}" , e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 9. 월드에 참여하기. (월드-유저 매핑 )
     * Name        : inviteJoinWorld
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PostMapping(value = "/world/user")
    public ResponseEntity<ResponseJsonObject> inviteJoinWorld(@RequestParam("worldinvitationCode") @Valid @Size(min = 6, max = 6, message = "인증 코드는 6자리 입니다.") String worldinvitationCode) throws YOPLEServiceException, ExecutionException, InterruptedException {
        try {

            WorldDto joinedWorld = userService.JoinWorld(worldinvitationCode);

            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(joinedWorld);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("월드 참여하기 Exception : {}" , e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("월드 참여하기 ERROR : {}" , e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 10. 알림 조회하기
     * Name        : notification
     * Author      : 조 준 희
     * History     : [2022-04-13] - 조 준 희 - Create
     */
    @GetMapping("/notification")
    public ResponseEntity<ResponseJsonObject> notification() throws Exception {
        try {

            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto) authentication.getPrincipal();

            NotiDto notis = userService.notificationList(userToken.getSuid());

            // 유저의 독바 알림 갱신 시간 최신화.
            userService.notiCheckDtUpdate(userToken.getSuid());

            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(notis);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("알림 조회하기 Exception : {}" , e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("알림 조회하기 ERROR : {}" , e.getMessage());
            throw e;
        }

    }

    /**
     * Description : 11. 월드 초대 응답하기.
     * - isAccept여부에 따라 수락하는지 거절하는지 판단.
     * - 월드 초대 코드 유효하지 않으면 WORLD_USER_CDOE_VALID_FAILED
     * - 사용자 이미 월드에 가입되어있으면 ALREADY_WORLD_MEMEBER
     * Name        : invite-response
     * Author      : 조 준 희
     * History     : [2022/04/17] - 조 준 희 - Create
     */
    @PostMapping("/invite-response")
    public ResponseEntity<ResponseJsonObject> inviteAccept(@RequestBody @Valid WorldInviteAccept invited) throws Exception {
        try {

            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto) authentication.getPrincipal();

            // suid 디코딩.
            invited.suidChange(CryptUtils.AES_Decode(invited.getUserSuid()));

            // 2. 월드 초대 응답하기 서비스
            WorldDto joinWorld = userService.inviteJoinWorld(invited, userToken.getSuid());

            // 3. 리턴 객체 생성
            ResponseJsonObject response;

            //거절하기 성공인 경우
            if (joinWorld.getWorldId().equals(0L)) {
                response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            } else if (joinWorld != null) {
                // 월드 수락하기 성공인 경우
                response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(joinWorld);
            } else {
                logger.error("월드 초대 응답하기 : 수락,거절이 아닌 다른 결과리턴.");
                throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR, "월드 초대 응답하기 실패.! 수락,거절이 아닌 다른 결과리턴.");
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("월드 초대 응답하기 Exception : {}", e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("월드 초대 응답하기 ERROR : {}", e.getMessage());
            throw e;
        }


    }

    /**
     * Description : 12. 리뷰 신고하기.
     * Name        : reviewReport
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @PostMapping("/review/report")
    public ResponseEntity<ResponseJsonObject> reviewReport(@RequestBody @Valid ReviewReportDto reviewReportDto) {
        try {
            ResponseJsonObject response;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            userService.reviewReport(userInfoDto.getSuid(), reviewReportDto);
            logger.debug("리뷰 신고하기 : {}",reviewReportDto.toString());

            // 응답 생성.
            response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("리뷰 신고하기 ERROR : {}", e.getMessage());
            throw e;
        }

    }

    /**
     * Description : 13. 사용자 신고하기.
     * Name        : report
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @PostMapping("/report")
    public ResponseEntity<ResponseJsonObject> report(@RequestBody @Valid UserReportDto userReportDto) throws Exception {
        try {

            ResponseJsonObject response;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            // suid 디코딩 작업.
            userReportDto.suidChange(CryptUtils.AES_Decode(userReportDto.getReportSuid()));

            userService.report(userInfoDto.getSuid(), userReportDto);
            logger.debug("사용자 신고하기 : {}",userReportDto.toString());

            // 응답 생성.
            response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("사용자 신고하기 ERROR : {}", e.getMessage());
            throw e;
        }

    }

    /**
     * Description : 14. 사용자 차단하기.
     * - 이미 차단된 유저인경우 ALREADY_USER_BLOCKING
     * Name        : block
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @PostMapping("/block")
    public ResponseEntity<ResponseJsonObject> block(@RequestBody @Valid UserBlockDto userBlockDto) throws Exception {
        try {
            ResponseJsonObject response;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();


            // suid 디코딩 작업.
            userBlockDto.suidChange(CryptUtils.AES_Decode(userBlockDto.getBlockSuid()));

            userService.block(userInfoDto.getSuid(), userBlockDto);
            logger.debug("사용자 차단하기 : {}", userBlockDto.toString());

            // 응답 생성.
            response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("사용자 차단하기 Exception : {}" , e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("사용자 차단하기 ERROR : {}", e.getMessage());
            throw e;
        }

    }

    /**
     * Description : 15. 사용자 차단리스트 조회
     * Name        : getBlock
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @GetMapping("/block")
    public ResponseEntity<ResponseJsonObject> getBlock() {
        try {
            ResponseJsonObject response;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            List<UserBlockedDto> blockUsers = userService.getBlock(userInfoDto.getSuid());
            logger.debug("사용자 차단리스트 조회 : 차단한 사용자 수 ({})",blockUsers.stream().count());

            // 응답 생성.
            response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(blockUsers);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("사용자 차단리스트 조회 ERROR : {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 16. 사용자 차단해제하기.
     * - 없는 차단 이력 요청할 경우 FORBIDDEN
     * - 사용자 차단 이력 아닌 경우 FORBIDDEN
     * Name        : blockCancel
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @PatchMapping("/block")
    public ResponseEntity<ResponseJsonObject> blockCancel(@RequestParam(required = true) @Valid @Positive Long blockId) throws YOPLEServiceException {
        try {

            ResponseJsonObject response;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            userService.blockCancel(userInfoDto.getSuid(), blockId);
            logger.debug("사용자 차단해제하기 : 사용자({})의 차단 ID({})해제 ",userInfoDto.getSuid(),blockId);

            // 응답 생성.
            response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("사용자 차단해제 Exception : {}", e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch (Exception e) {
            logger.error("사용자 차단해제 ERROR : {}", e.getMessage());
            throw e;
        }

    }

    /**
     * 17. 회원탈퇴
     *
     * @return
     * @throws YOPLEServiceException
     */
    @DeleteMapping("/withdrawal")
    public ResponseEntity<ResponseJsonObject> userWithdrawal() throws YOPLEServiceException {

        userService.userWithdrawal();

        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }


    /**
     * Description : 18. 최신 알림 여부 확인
     * Name        : newNotiCheck
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping("/newNotiCheck")
    public ResponseEntity<ResponseJsonObject> newNotiCheck() throws YOPLEServiceException {

        try {
            ResponseJsonObject responseJsonObject;

            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto) authentication.getPrincipal();

            // 2. 독바 최신 알림 있는지 여부 체크
            Boolean newNotiYN = userService.newNotiCheck(userToken.getSuid());

            // 3. 응답 생성.
            Map<String, Object> responseMap = new HashMap<>();

            responseMap.put("newNotiYN", (newNotiYN) ? "Y" : "N"  );

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            responseJsonObject.setData(responseMap);

            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);

        } catch (YOPLEServiceException e) {
            logger.debug("사용자 상세정보 조회 Exception : {}", e.getResponseJsonObject().getMeta().toString());
            throw e;
        } catch(Exception e){
            logger.error("최신 알림 여부 확인 ERROR : {}" , e.getMessage());
            throw e;
        }
    }

}
