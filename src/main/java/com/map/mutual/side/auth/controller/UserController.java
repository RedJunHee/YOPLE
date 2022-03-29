package com.map.mutual.side.auth.controller;


import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.JWTRefreshTokenLogEntity;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.auth.svc.AuthService;
import com.map.mutual.side.auth.svc.UserService;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.filter.AuthorizationCheckFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
    @PostMapping("/signUp")
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

    @GetMapping("/checkUserId")
    public ResponseEntity<ResponseJsonObject> checkUserId(@RequestParam String id) {
        ResponseJsonObject response;
        try{
            if(userInfoRepo.findByUserId(id) == null) {
                response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            } else {
                response =  ResponseJsonObject.withStatusCode(ApiStatusCode.PARAMETER_CHECK_FAILED);
            }
        }catch (YOPLEServiceException e) {
            throw e;
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/user/bySuid")
    public ResponseEntity<ResponseJsonObject> getUserById(@RequestParam String id) {
        ResponseJsonObject response;
        try{
            UserInfoDto userInfoDto = userService.getUserById(id);

            response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            response.setData(userInfoDto);
        }catch (YOPLEServiceException e) {
            throw e;
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/user/byPhone")
    public ResponseEntity<ResponseJsonObject> getUserByPhone(@RequestParam String phone) {
        ResponseJsonObject response;
        try{
            UserInfoDto userInfoDto = userService.getUserByPhone(phone);

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

            response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            response.setData(userInfoDto);
        }catch (YOPLEServiceException e) {
            throw e;
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
