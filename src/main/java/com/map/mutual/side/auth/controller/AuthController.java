package com.map.mutual.side.auth.controller;

import com.map.mutual.side.auth.component.SmsSender;
import com.map.mutual.side.auth.model.dto.JwtTokenDto;
import com.map.mutual.side.auth.model.dto.SMSAuthReqeustDto;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.JWTRefreshTokenLogEntity;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.svc.AuthService;
import com.map.mutual.side.common.JwtTokenProvider;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.filter.AuthorizationCheckFilter;
import com.map.mutual.side.common.utils.CryptUtils;
import com.map.mutual.side.common.utils.YOPLEUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
@Validated
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private AuthService authService;
    private CryptUtils cryptUtils;
    private ModelMapper modelMapper;
    private SmsSender smsSender;

    @Autowired
    public AuthController(AuthService authService,
                          CryptUtils cryptUtils, ModelMapper modelMapper,
                          SmsSender smsSender) {
        this.authService = authService;
        this.cryptUtils = cryptUtils;
        this.modelMapper = modelMapper;
        this.smsSender = smsSender;
    }

    /**
     * Description : SMS 인증번호 요청하기.
     * Name        : smsAuthenticationRequest
     * Author      : 조 준 희
     * History     : [2022/04/12] - 조 준 희 - Create
     */
    @PostMapping("/sms-authentication-request")
    public ResponseEntity<ResponseJsonObject> smsAuthenticationRequest( @RequestBody @Valid SMSAuthReqeustDto smsAuthReqeustDTO) throws MethodArgumentNotValidException, NoSuchAlgorithmException, KeyStoreException, IOException, InvalidKeyException, KeyManagementException {

        try {

            // 2. SMS 인증 번호 생성
            //String smsAuthNum = YOPLEUtils.getSMSAuth();
            String smsAuthNum = "0000";

            // 3. 로그 저장
            authService.smsAuthNumSave(smsAuthReqeustDTO, smsAuthNum);

            // 2. 핸드폰 번호 인증 요청
            logger.debug(String.format("SMS 인증번호 요청하기 : SMS 문자 Call start"));
            smsSender.sendAuthMessage(smsAuthReqeustDTO.getPhone(), smsAuthNum);
            logger.debug(String.format("SMS 인증번호 요청하기 : SMS 문자 Call end"));

            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);

        }catch(Exception e){
            logger.error("SMS 인증번호 요청하기 ERROR : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Description : SMS 인증번호 확인 요청하기.
     * Name        : smsAuthenticationResponse
     * Author      : 조 준 희
     * History     : [2022/04/12] - 조 준 희 - Create
     */
    @PostMapping("/sms-authentication-response")
    public ResponseEntity<ResponseJsonObject> smsAuthenticationResponse(@RequestBody @Valid SMSAuthReqeustDto smsAuthReqeustDTO) throws Exception {
        try {

            // 응답 확인
            authService.smsAuthNumResponse(smsAuthReqeustDTO);

            //
            UserEntity user = authService.findOneByPhone(smsAuthReqeustDTO.getPhone());

            if(user == null){
                logger.debug("SMS 인증번호 확인 요청하기 : 사용자({})의 사용자 정보를 찾을 수 없음.",smsAuthReqeustDTO.getPhone());
                throw new YOPLEServiceException(ApiStatusCode.USER_NOT_FOUND);
            }

            UserInfoDto userInfoDto = modelMapper.map(user, UserInfoDto.class);

            //JWT 발급.
            String accessJwt = authService.makeAccessJWT(userInfoDto);
            String refreshJwt = authService.makeRefreshJWT(user.getSuid());

            JWTRefreshTokenLogEntity log = JWTRefreshTokenLogEntity.builder()
                    .refreshToken(refreshJwt)
                    .userSuid(user.getSuid())
                    .build();

            authService.saveJwtLog(log);
            JwtTokenDto jwtToken = JwtTokenDto.builder().accessToken(accessJwt).refreshToken(refreshJwt).build();


            logger.debug("SMS 인증번호 확인 요청하기 : 사용자({})의 정보 반환 - 액세스 토큰({}), 리프레시 토큰({})",smsAuthReqeustDTO.getPhone(),accessJwt,refreshJwt );

            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(jwtToken),HttpStatus.OK);

        }catch(YOPLEServiceException yopleServiceException)
        {
            logger.debug("SMS 인증번호 확인 요청하기 Exception : {}", yopleServiceException.getResponseJsonObject().getMeta().toString());
            throw yopleServiceException;
        }
        catch(Exception e){
            logger.error("SMS 인증번호 확인 요청하기 ERROR : {}",e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 액세스 토큰 갱신하기.
     * Name        : jwtAccessRefresh
     * Author      : 조 준 희
     * History     : [2022/04/12] - 조 준 희 - Create
     */
    @PostMapping("/access-refresh")
    public ResponseEntity<ResponseJsonObject> jwtAccessRefresh(@RequestHeader(value = AuthorizationCheckFilter.REFRESH_TOKEN, required = false) @Valid @NotBlank(message = "리프레시 토큰이 널이거나 빈값입니다.") String refreshToken) throws Exception {
        try{
            String jwt ;

            // 1. 토큰 유효성 체크.
            if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {     // JWT 토큰이 존재하는지 확인
                refreshToken = refreshToken.substring(7);           // "Bearer"를 제거한 accessToken 반환
            }
            else{
                logger.debug("Access Token 갱신 : 리프레시 토큰 존재하지 않음. ( refresh token : {})", refreshToken);
                refreshToken = null;
            }


            jwt = authService.JWTAccessRefresh(refreshToken);
            JwtTokenDto jwtToken = JwtTokenDto.builder().accessToken(jwt).build();


            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(jwtToken),HttpStatus.OK );

        }catch(YOPLEServiceException e)
        {
            logger.debug("액세스 토큰 갱신하기 ERROR : " + e.getResponseJsonObject().getMeta().toString());
            throw e;
        }catch(Exception e)
        {
            logger.error("액세스 토큰 갱신하기 ERROR : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 리프레시 토큰 갱신
     * Name        : jwtRefreshRefresh
     * Author      : 조 준 희
     * History     : [2022/04/12] - 조 준 희 - Create
     */
    @PostMapping("/refresh-refresh")
    public ResponseEntity<ResponseJsonObject> jwtRefreshRefresh() throws Exception {

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userDetails = (UserInfoDto)authentication.getPrincipal();

            String suid = userDetails.getSuid();


            String refreshToken = authService.makeRefreshJWT(suid);

            JWTRefreshTokenLogEntity log = JWTRefreshTokenLogEntity.builder().userSuid(suid).refreshToken(refreshToken).build();
            authService.saveJwtLog(log);

            JwtTokenDto jwtToken = JwtTokenDto.builder().refreshToken(refreshToken).build();

            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(jwtToken),HttpStatus.OK );

        }catch(Exception e)
        {
            logger.error("리프레시 토큰 갱신하기 ERROR : " + e.getMessage());
            throw e;
        }
    }
}
