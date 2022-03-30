package com.map.mutual.side.auth.controller;


import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.dto.UserWorldInvitionDto;
import com.map.mutual.side.auth.model.entity.JWTRefreshTokenLogEntity;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.auth.svc.AuthService;
import com.map.mutual.side.auth.svc.UserService;
import com.map.mutual.side.auth.svc.impl.UserServiceImpl;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.filter.AuthorizationCheckFilter;
import com.map.mutual.side.world.model.dto.WorldDto;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class UserController {

    private final Logger logger = LogManager.getLogger(UserController.class);
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoRepo userInfoRepo;


    /**
     * 유저 회원가입
     * @param userInfoDto
     * @return
     * @throws Exception
     */
    @PostMapping("/signup")
    public ResponseEntity<ResponseJsonObject> smsSignUp(@Validated @RequestBody UserInfoDto userInfoDto) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            String suid = "YO";
            LocalDate date = LocalDate.now();

            suid += String.format("%02d",date.getYear())
                    +String.format("%02d",date.getMonthValue())
                    +String.format("%02d",date.getDayOfMonth())
                    +userInfoDto.getPhone().substring(3);


            userInfoDto.setSuid(suid);

            // 회원 가입 된 유저의 정보 반환
            UserInfoDto user = authService.signUp(userInfoDto);

            //JWT 발급.
            String accessJwt = authService.makeAccessJWT(user);
            String refreshJwt = authService.makeRefreshJWT(user.getSuid());

            JWTRefreshTokenLogEntity log = JWTRefreshTokenLogEntity
                    .builder().refreshToken(refreshJwt).userSuid(user.getSuid())
                    .build();

            authService.saveJwtLog(log);

            httpHeaders.add(AuthorizationCheckFilter.ACCESS_TOKEN, accessJwt);
            httpHeaders.add(AuthorizationCheckFilter.REFRESH_TOKEN, refreshJwt);

        } catch (Exception e) {
            throw e;
        }
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK),httpHeaders, HttpStatus.OK);
    }

    /** 월드 초대 수락하기 (월드-유저 매핑 )*/
    @PostMapping(value = "/world/user")
    public ResponseEntity<ResponseJsonObject> inviteJoinWorld(@RequestParam("worldinvitationCode") String worldinvitationCode){
        try{

            WorldDto joinedWorld = userService.inviteJoinWorld( worldinvitationCode);

            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(joinedWorld);

            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(Exception e)
        {
            logger.error("WorldController inviteJoinWorld Failed.!! : " + e.getMessage());
            throw e;
        }
    }

    @GetMapping("/check-userid")
    public ResponseEntity<ResponseJsonObject> checkUserId(@RequestParam("userId") String userId) {
        ResponseJsonObject response;
        try{
            if(userInfoRepo.findByUserId(userId) == null) {
                response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            } else {
                response =  ResponseJsonObject.withStatusCode(ApiStatusCode.PARAMETER_CHECK_FAILED);
            }
        }catch (YOPLEServiceException e) {
            throw e;
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/find-user")
    public ResponseEntity<ResponseJsonObject> findUserByIdOrPhone(@RequestParam String userId,
                                                                  @RequestParam String phone) {
        ResponseJsonObject response;
        try{
            UserInfoDto userInfoDto;

            userInfoDto = userService.findUser(userId, phone);

            response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            response.setData(userInfoDto);
        }catch (YOPLEServiceException e) {
            throw e;
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/world/users")
    public ResponseEntity<ResponseJsonObject> worldUsers(@RequestParam long worldId) {
        ResponseJsonObject response;
        try{
            List<UserInWorld> userInfoDto = userService.worldUsers(worldId);

            Map<String, Object> Users = new HashMap<>();

            Users.put("users", userInfoDto);

            response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            response.setData(Users);

        }catch (YOPLEServiceException e) {
            throw e;
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //사용자 상세정보 조회.
    @GetMapping("/user")
    public ResponseEntity<ResponseJsonObject> userDetails() {

        ResponseJsonObject responseJsonObject;

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto)authentication.getPrincipal();

            UserInfoDto userDetails = userService.userDetails(userToken.getSuid());

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            responseJsonObject.setData(userDetails);

            return new ResponseEntity<>(responseJsonObject,HttpStatus.OK);

        }catch(YOPLEServiceException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
    }

    //사용자 정보 수정
    @PatchMapping("/user")
    public ResponseEntity<ResponseJsonObject> userInfoUpdate(@RequestParam String userId,
                                                             @RequestParam String profileUrl){

        ResponseJsonObject responseJsonObject ;

        try{

            //둘 중에 하나도 안들어오면 파라미터 체크 에러.
            if(StringUtil.isNullOrEmpty(userId) && StringUtil.isNullOrEmpty(profileUrl))
                throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto) authentication.getPrincipal();

            UserInfoDto updatedUser = userService.userInfoUpdate(userToken.getSuid(), userId,profileUrl);

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(updatedUser);

            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);

        }catch(YOPLEServiceException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
    }


    //사용자 로그아웃 리프레시 토큰 삭제.
    @DeleteMapping("/user")
    public ResponseEntity<ResponseJsonObject> userLogout() {

        ResponseJsonObject responseJsonObject;

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto)authentication.getPrincipal();

            userService.userLogout(userToken.getSuid());

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            return new ResponseEntity<>(responseJsonObject,HttpStatus.OK);

        }catch(YOPLEServiceException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
    }

    //사용자 상세정보 조회.
    @PostMapping("/user/world")
    public ResponseEntity<ResponseJsonObject> userWorldInviting(@RequestBody UserWorldInvitionDto userWorldInvitionDto) {

        ResponseJsonObject responseJsonObject;

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userToken = (UserInfoDto)authentication.getPrincipal();


            userService.userLogout(userToken.getSuid());

            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            return new ResponseEntity<>(responseJsonObject,HttpStatus.OK);

        }catch(YOPLEServiceException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
    }

}
