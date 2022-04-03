package com.map.mutual.side.auth.controller;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.JWTRefreshTokenLogEntity;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.common.JwtTokenProvider;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.filter.AuthorizationCheckFilter;
import com.map.mutual.side.common.utils.CryptUtils;
import com.map.mutual.side.common.utils.YOPLEUtils;
import com.map.mutual.side.auth.model.dto.SMSAuthReqeustDto;
import com.map.mutual.side.auth.svc.AuthService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Class       : AuthController
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-11] - 조 준희 - Class Create
 */
@RestController
@RequestMapping(value="/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    private AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private CryptUtils cryptUtils;
    private ModelMapper modelMapper;

    @Autowired
    public AuthController(AuthService authService, JwtTokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, CryptUtils cryptUtils, ModelMapper modelMapper) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.cryptUtils = cryptUtils;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/sms-authentication-request")
    public ResponseEntity<ResponseJsonObject> smsAuthenticationRequest(@RequestBody @Valid SMSAuthReqeustDto smsAuthReqeustDTO) throws MethodArgumentNotValidException, NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, KeyManagementException {

        try {
            // 1. 핸드폰 번호 벨리데이션
                // 생략...

            // 2. SMS 인증 번호 생성
            String smsAuthNum = YOPLEUtils.getSMSAuth();

            // 2. 핸드폰 번호 인증 요청
            authService.sendMessageTest(smsAuthReqeustDTO.getPhone(), smsAuthNum);

            // 3. 로그 저장
            authService.smsAuthNumSave(smsAuthReqeustDTO, smsAuthNum);
        }
        catch(Exception e){
            throw e;
        }
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }

    @PostMapping("/sms-authentication-response")
    public ResponseEntity<ResponseJsonObject> smsAuthenticationResponse(@RequestBody SMSAuthReqeustDto smsAuthReqeustDTO) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            // 응답 확인
            authService.smsAuthNumResponse(smsAuthReqeustDTO);

            //
            UserEntity user = authService.findOneByPhone(smsAuthReqeustDTO.getPhone());

            if(user == null)
                throw new YOPLEServiceException(ApiStatusCode.USER_NOT_FOUND);

            UserInfoDto userInfoDto = modelMapper.map(user, UserInfoDto.class);


            //JWT 발급.
            String accessJwt = authService.makeAccessJWT(userInfoDto);
            String refreshJwt = authService.makeRefreshJWT(user.getSuid());

            JWTRefreshTokenLogEntity log = JWTRefreshTokenLogEntity.builder()
                    .refreshToken(refreshJwt)
                    .userSuid(user.getSuid())
                    .build();

            authService.saveJwtLog(log);

            httpHeaders.add(AuthorizationCheckFilter.ACCESS_TOKEN, accessJwt);
            httpHeaders.add(AuthorizationCheckFilter.REFRESH_TOKEN, refreshJwt);


        }catch(YOPLEServiceException yopleServiceException)
        {
            throw yopleServiceException;
        }
        catch(Exception e){
            throw e;
        }
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), httpHeaders,HttpStatus.OK);
    }

    // 액세스 토큰 갱신
    @PostMapping("/access-refresh")
    public ResponseEntity<ResponseJsonObject> jwtAccessRefresh(@RequestHeader(value = AuthorizationCheckFilter.REFRESH_TOKEN) String refreshToken) throws Exception {
        try{
            String jwt ;
            HttpHeaders headers = new HttpHeaders();

            jwt = authService.JWTAccessRefresh(refreshToken);

            headers.add(AuthorizationCheckFilter.ACCESS_TOKEN,jwt);

            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK),headers,HttpStatus.OK );

        }catch(YOPLEServiceException e)
        {
            throw e;
        }catch(Exception e)
        {
            throw e;
        }
    }
    // 리프레시 토큰 갱신
    @PostMapping("/refresh-refresh")
    public ResponseEntity<ResponseJsonObject> jwtRefreshRefresh() throws Exception {

        try{
            HttpHeaders headers = new HttpHeaders();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userDetails = (UserInfoDto)authentication.getPrincipal();

            String suid = userDetails.getSuid();

            String refreshToken = authService.makeRefreshJWT(suid);

            JWTRefreshTokenLogEntity log = JWTRefreshTokenLogEntity.builder().userSuid(suid).refreshToken(refreshToken).build();

            authService.saveJwtLog(log);

            headers.add(AuthorizationCheckFilter.REFRESH_TOKEN,refreshToken);

            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK),headers,HttpStatus.OK );

        }catch(YOPLEServiceException e)
        {
            throw e;
        }catch(Exception e)
        {
            throw e;
        }
    }
}
