package com.map.mutual.side.auth.svc.impl;

import com.map.mutual.side.auth.model.dto.SMSAuthReqeustDto;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.JWTRefreshTokenLogEntity;
import com.map.mutual.side.auth.model.entity.SMSRequestLogEntity;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.repository.JWTRepo;
import com.map.mutual.side.auth.repository.SMSLogRepo;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.auth.svc.AuthService;
import com.map.mutual.side.common.JwtTokenProvider;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Class       : SmsServiceImpl
 * Author      : 김 재 중
 * Description : Class Description
 * History     : [2022-03-13] - 조준희 - smsAuthNum 저장 서비스 생성,
 */
@Service
@Log4j2
public class AuthServiceImpl implements AuthService {
    private final static Logger logger = LogManager.getLogger(AuthServiceImpl.class);
    private SMSLogRepo smsLogRepo;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserInfoRepo userInfoRepo;
    private final JWTRepo jwtRepo;
    private ModelMapper modelMapper;

    @Autowired
    public AuthServiceImpl(SMSLogRepo smsLogRepo,
                           JwtTokenProvider tokenProvider,
                           AuthenticationManagerBuilder authenticationManagerBuilder,
                           UserInfoRepo userInfoRepo,
                           JWTRepo jwtRepo,
                           ModelMapper modelMapper) {
        this.smsLogRepo = smsLogRepo;
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userInfoRepo = userInfoRepo;
        this.jwtRepo = jwtRepo;
        this.modelMapper = modelMapper;;
    }



    @Override
    public void smsAuthNumSave(SMSAuthReqeustDto smsAuthReqeustDTO, String smsAuthNum) {

        // 2. SMSRequestLog 생성
        SMSRequestLogEntity smsLog = SMSRequestLogEntity.builder().phone(smsAuthReqeustDTO.getPhone())
                .requestAuthNum(smsAuthNum)
                .duid(smsAuthReqeustDTO.getDuid())
                .build();

        // 3. Log 저장
        smsLogRepo.save(smsLog);
    }

    @Override
    public void smsAuthNumResponse(SMSAuthReqeustDto smsAuthResponseDTO) throws YOPLEServiceException {

        try {
            SMSRequestLogEntity smslog = smsLogRepo
                    .findTop1ByPhoneAndCreateTimeBetweenOrderByCreateTimeDesc(
                            smsAuthResponseDTO.getPhone(),
                            LocalDateTime.now().minusMinutes(5),
                            LocalDateTime.now());
            if (smslog == null) {
                throw new YOPLEServiceException(ApiStatusCode.AUTH_META_NOT_MATCH);
            }

            // 인증 코드 확인
            if(smslog.getRequestAuthNum().equals(smsAuthResponseDTO.getResponseAuthNum()))
            {
                // match
                smslog.setResponseAuthNum(smsAuthResponseDTO.getResponseAuthNum());
                smsLogRepo.save(smslog);
                log.debug("SMS Auth Number \"smsAuthNumResponse\" Update!!");
            }
            else
            {
                log.debug("SMSAuthNum Not Match!!");
                throw new YOPLEServiceException(ApiStatusCode.AUTH_META_NOT_MATCH);
            }

        }catch(YOPLEServiceException e)
        {
            throw e;
        }

    }

    public UserEntity findOneByPhone(String phone){
        try{
            return userInfoRepo.findOneByPhone(phone);
        }catch(Exception e) {
            throw e;
        }
    }

    @Override
    public String JWTAccessRefresh(String refreshToken) throws YOPLEServiceException, Exception {

            //Refresh 벨리데이션 + 유효기간 체크.
            if (tokenProvider.validateToken(refreshToken) == false) {
                throw new YOPLEServiceException(ApiStatusCode.UNAUTHORIZED);
            }

            // 액세스 토큰 갱신
            String suid = ((UserInfoDto)(tokenProvider.getAccessAuthentication(refreshToken).getPrincipal())).getSuid();

            // JWT 리플레시 로그 불러옴.
            JWTRefreshTokenLogEntity jwtRefreshTokenLogEntity = jwtRepo.findOneByUserSuid(suid);

            // DB 저장된 리플레시와 요청으로 받은 리플레시가 다를 경우 Exception
            if (jwtRefreshTokenLogEntity == null || jwtRefreshTokenLogEntity.getRefreshToken().equals(refreshToken) == false) {
                logger.debug(String.format("Access Token 갱신 : INPUT 리프레시 토큰과 DB 리프레시 토큰이 다름"));
                throw new YOPLEServiceException(ApiStatusCode.UNAUTHORIZED);
            }

            UserInfoDto userInfoDto = UserInfoDto.builder().suid(suid).build();
            String accessToken = makeAccessJWT(userInfoDto);

            return accessToken;

    }

    @Override
    public void saveJwtLog(JWTRefreshTokenLogEntity log) throws Exception {
        try {

            if(jwtRepo.findOneByUserSuid(log.getUserSuid()) != null )
                log.isPersist();

            jwtRepo.save(log);

        }catch(Exception e)
        {
            logger.error(String.format("JWT 저장하기 실패."));
            throw e;
        }
    }

    public String makeAccessJWT(UserInfoDto user) throws Exception {
        Authentication authentication = null ;
        String jwt = "";

        try {
            authentication =  new UsernamePasswordAuthenticationToken(user,null);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            jwt = tokenProvider.createAccessTokenFromAuthentication(authentication);
        }catch(AuthenticationException ex)  // 인증 절차 실패시 리턴되는 Exception
        {
            //logger.debug("AuthController Auth 체크 실패 "+ ex.getMessage());
            throw ex;
        }catch(Exception ex)
        {
            //logger.error("AuthController Exception : " + ex.getMessage());
            throw ex;
        }   // 체크 필요!

        return jwt;
    }

    public String makeRefreshJWT(String suid) throws Exception {
        String jwt;
        try {
            jwt = tokenProvider.createRefreshTokenFromAuthentication(suid);
        }catch(Exception ex)
        {
            logger.error(" Refresh Token 생성 ERROR : " + ex.getMessage());
            throw ex;
        }   // 체크 필요!

        return jwt;
    }

}
