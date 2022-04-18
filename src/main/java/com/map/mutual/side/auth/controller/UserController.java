package com.map.mutual.side.auth.controller;


import com.map.mutual.side.auth.model.dto.*;
import com.map.mutual.side.auth.model.dto.notification.NotiDto;
import com.map.mutual.side.auth.model.entity.JWTRefreshTokenLogEntity;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.auth.svc.AuthService;
import com.map.mutual.side.auth.svc.UserService;
import com.map.mutual.side.common.config.BeanConfig;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.filter.AuthorizationCheckFilter;
import com.map.mutual.side.world.model.dto.WorldDto;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import io.jsonwebtoken.Jwt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

/**
 * fileName       : UserController
 * author         : kimjaejung
 * createDate     : 2022/03/16
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/16        kimjaejung       최초 생성
 *
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    private final Logger logger = LogManager.getLogger(UserController.class);
    private AuthService authService;
    private UserService userService;
    private UserInfoRepo userInfoRepo;

    @Autowired
    public UserController(AuthService authService, UserService userService, UserInfoRepo userInfoRepo) {
        this.authService = authService;
        this.userService = userService;
        this.userInfoRepo = userInfoRepo;
    }

    /**
     * Description : 사용자 회원가입.
     * Name        : smsSignUp
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<ResponseJsonObject> smsSignUp(@RequestBody @Valid UserInfoDto userInfoDto) throws Exception {
        try {

            if ( userInfoDto.getUserTOSDto().getUserInfoYN().equals("Y") == false
                || userInfoDto.getUserTOSDto().getLocationInfoYN().equals("Y") == false
                || userInfoDto.getUserTOSDto().getAgeCollectionYN().equals("Y") == false
                || userInfoDto.getUserTOSDto().getServiceTosYN().equals("Y") == false
            ){
                throw new YOPLEServiceException(ApiStatusCode.USER_TOS_INFO_VALID_FAILED);
            }

            String suid = "YO";
            LocalDate date = LocalDate.now();

            suid += String.format("%02d",date.getYear())
                    +String.format("%02d",date.getMonthValue())
                    +String.format("%02d",date.getDayOfMonth())
                    +userInfoDto.getPhone().substring(3);


            userInfoDto.setSuid(suid);

            // 회원 가입 된 유저의 정보 반환
            UserInfoDto user = userService.signUp(userInfoDto);

            //JWT 발급.
            String accessJwt = authService.makeAccessJWT(user);
            String refreshJwt = authService.makeRefreshJWT(user.getSuid());

            JWTRefreshTokenLogEntity log = JWTRefreshTokenLogEntity
                    .builder().refreshToken(refreshJwt).userSuid(user.getSuid())
                    .build();

            authService.saveJwtLog(log);

            JwtTokenDto jwtTokenDto = JwtTokenDto.builder().accessToken(accessJwt).refreshToken(refreshJwt).build();

            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(jwtTokenDto), HttpStatus.OK);


        }catch(YOPLEServiceException e){
            logger.error("사용자 회원가입 실패. : " + e.getResponseJsonObject().getMeta().getErrorType());
            throw e;
        }catch (Exception e) {
            throw e;
        }
    }

    /**
     * Description : 월드에 참여하기. (월드-유저 매핑 )
     * Name        : inviteJoinWorld
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PostMapping(value = "/world/user")
    public ResponseEntity<ResponseJsonObject> inviteJoinWorld(@RequestParam("worldinvitationCode") @Valid @Size(min = 6, max = 6,message = "인증 코드는 6자리 입니다.") String worldinvitationCode){
        try{

            WorldDto joinedWorld = userService.JoinWorld( worldinvitationCode);

            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(joinedWorld);

            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(YOPLEServiceException e) {
            logger.error("월드에 참여하기 실패. : " + e.getResponseJsonObject().getMeta().getErrorType());
            throw e;
        }catch(Exception e)
        {
            logger.error("WorldController inviteJoinWorld Failed.!! : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 유저 ID 중복체크
     * Name        : checkUserId
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping("/check-userid")
    public ResponseEntity<ResponseJsonObject> checkUserId(@RequestParam("userId") @Valid
                                                              @Pattern(regexp = BeanConfig.userIdRegexp, message = "ID가 올바르지 않습니다.") String userId) {
        ResponseJsonObject response;
        try{
            if(userInfoRepo.findByUserId(userId) == null) {
                response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            } else {
                response =  ResponseJsonObject.withStatusCode(ApiStatusCode.USER_ID_OVERLAPS);
            }
        }catch (YOPLEServiceException e) {
            logger.error("유저 ID 중복체크 실패 . : " + e.getResponseJsonObject().getMeta().getErrorType());
            throw e;
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Description : 월드 초대하기 전 사용자 검색에 사용되는 API
     * Name        : findUserByIdOrPhone
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping("/find-user")
    public ResponseEntity<ResponseJsonObject> findUserByIdOrPhone(@RequestParam(required = false) @Valid     @Pattern(regexp = BeanConfig.userIdRegexp,
                                                                            message = "ID가 올바르지 않습니다.")  String userId,
                                                                  @RequestParam(required = false) @Valid @Pattern(regexp = BeanConfig.phoneRegexp,
                                                                          message = "핸드폰 번호가 올바르지 않습니다.") String phone) {
        ResponseJsonObject response;
        try{

            // 1. 둘 중에 하나도 안들어오면 파라미터 체크 에러.
            if(StringUtil.isNullOrEmpty(userId) && StringUtil.isNullOrEmpty(phone))
                throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED,"사용자 ID 또는 핸드폰 번호 중에 한 정보 이상 요청해야합니다.");

            UserInfoDto userInfoDto;

            userInfoDto = userService.findUser(userId, phone);

            response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            response.setData(userInfoDto);
        }catch (YOPLEServiceException e) {
            throw e;
        }catch(Exception e){
            logger.error(e.getMessage());
            throw e;
        }


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Description : 월드에서 참여자 리스트 화면에 사용되는 API
     * Name        :  worldUsers
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping("/world/users")
    public ResponseEntity<ResponseJsonObject> worldUsers(@RequestParam @Valid @NotNull long worldId) {
        ResponseJsonObject response;
        try{

            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto)authentication.getPrincipal();

            // 1. 월드에 참여 중인 사용자 조회.
            List<UserInWorld> userInfoDto = userService.worldUsers(worldId, userToken.getSuid());

            // 2. 응답 객체 설정
            Map<String, Object> Users = new HashMap<>();

            Users.put("users", userInfoDto);
            response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            response.setData(Users);

            // 3. 리턴.
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (YOPLEServiceException e) {
            logger.error("월드 참여자 조회 실패 :" + e.getResponseJsonObject().getMeta().getErrorType());
            throw e;
        }

    }

    /**
     * Description : 사용자 상세정보 조회
     * Name        : userDetails
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping("/user")
    public ResponseEntity<ResponseJsonObject> userDetails() {
        ResponseJsonObject responseJsonObject;

        try{

            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto)authentication.getPrincipal();

            // 2. 사용자 상세정보 조회
            UserInfoDto userDetails = userService.userDetails(userToken.getSuid());

            // 3. 사용자 최근 접속 월드 ID 조회
            Long recentAccessWorldID = userService.getRecentAccessWorldID(userToken.getSuid());

            // 4. 응답 생성.
            Map<String, Object> responseMap = new HashMap<>();

            responseMap.put("recentWorldId",recentAccessWorldID);
            responseMap.put("user", userDetails);

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            responseJsonObject.setData(responseMap);


            return new ResponseEntity<>(responseJsonObject,HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.error("사용자 상세정보 조회 실패. : " + e.getResponseJsonObject().getMeta().getErrorType());
            throw e;
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Description : 사용자 상세정보 수정.
     * Name        :  userInfoUpdate
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PatchMapping("/user")
    public ResponseEntity<ResponseJsonObject> userInfoUpdate(@RequestParam(required = false) @Valid @Pattern(regexp = BeanConfig.userIdRegexp,
                                                                        message = "ID가 올바르지 않습니다.") String userId,
                                                             @RequestParam(required = false) String profileUrl){

        ResponseJsonObject responseJsonObject ;

        try{

            // 1. 둘 중에 하나도 안들어오면 파라미터 체크 에러.
            if(StringUtil.isNullOrEmpty(userId) && StringUtil.isNullOrEmpty(profileUrl))
                throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED);

            // 2. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto) authentication.getPrincipal();

            UserInfoDto updatedUser = userService.userInfoUpdate(userToken.getSuid(), userId,profileUrl);

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(updatedUser);

            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.error("사용자 상세정보 수정 실패. : " + e.getResponseJsonObject().getMeta().getErrorType());
            throw e;
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Description : 사용자 로그아웃 - 리프레시 토큰 삭제처리
     * Name        :  userLogout
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @DeleteMapping("/user")
    public ResponseEntity<ResponseJsonObject> userLogout() {

        ResponseJsonObject responseJsonObject;

        try{
            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto)authentication.getPrincipal();

            userService.userLogout(userToken.getSuid());

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            return new ResponseEntity<>(responseJsonObject,HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.error("사용자 로그아웃 실패. : "+ e.getResponseJsonObject().getMeta().getErrorType());
            throw e;
        }catch(Exception e){
            throw e;
        }
    }

    
    /**
     * Description : 월드에 사용자 초대하기. PUSH성
     * - 초대자가 월드에 참여중이 아닌 경우 YOPLEServiceException(FORBIDDEN) Throw
     * - 이미 월드에 참여 중인 경우 YOPLEServiceException(ALREADY_WORLD_MEMEBER) Throw
     * - 사용자가 이미 초대 대기 중인 경우 YOPLEServiceException() Throw
     * Name        : userWorldInviting
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PostMapping("/user/world")
    public ResponseEntity<ResponseJsonObject> userWorldInviting(@RequestBody @Valid UserWorldInvitionDto userWorldInvitionDto) {

        ResponseJsonObject responseJsonObject;

        try{

            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto)authentication.getPrincipal();

            // SUID로 초대하기 요청 온 경우. => 회원 유저임.
            if( StringUtil.isNullOrEmpty( userWorldInvitionDto.getTargetSuid()) == false )
                userService.userWorldInviting(userToken.getSuid(),userWorldInvitionDto.getTargetSuid(), userWorldInvitionDto.getWorldId());

            else if(StringUtil.isNullOrEmpty(userWorldInvitionDto.getPhone()) == false ){
                // TODO: 2022/04/17   유저의 핸드폰 번호 or ID 핸드폰 번호로 일단 개발진행
                userService.unSignedUserWorldInviting(userToken.getSuid(),userWorldInvitionDto.getPhone(),userWorldInvitionDto.getWorldId());
            }else{
                throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR,"월드 초대 실패.");

            }

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            return new ResponseEntity<>(responseJsonObject,HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.error("월드 사용자 초대하기 실패. : " + e.getResponseJsonObject().getMeta().getErrorType());
            throw e;
        }catch(Exception e){
            throw e;
        }
    }




    /**
     * Description : 알림 메시지 조회
     * Name        : notification
     * Author      : 조 준 희
     * History     : [2022-04-13] - 조 준 희 - Create
     */
    @GetMapping("/notification")
    public ResponseEntity<ResponseJsonObject> notification(){
        try{

            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto)authentication.getPrincipal();

            NotiDto notis = userService.notificationList(userToken.getSuid());

            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(notis);

            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(YOPLEServiceException e)
        {
            logger.error("사용자 알림 메시지 조회 실패. : " + e.getResponseJsonObject().getMeta().getErrorType());
            throw e;
        }catch(Exception e)
        {
            logger.error(e.getMessage());
            throw e;
        }

    }


    /**
     * Description : 월드 초대 응답하기. isAccept여부에 따라 수락하는지 거절하는지 판단.
     * Name        :
     * Author      : 조 준 희
     * History     : [2022/04/17] - 조 준 희 - Create
     */
    @PostMapping("/invite")
    public ResponseEntity<ResponseJsonObject> inviteAccept(@RequestBody  @Valid WorldInviteAccept invited){

        try {

            // 1. 토큰에서 사용자 SUID 정보 조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto) authentication.getPrincipal();



        }catch(YOPLEServiceException e) {
            logger.error("월드 초대 응답하기 실패.! : "+ e.getResponseJsonObject().getMeta().getErrorMsg());
            throw e;
        }


        return null;
    }

}
