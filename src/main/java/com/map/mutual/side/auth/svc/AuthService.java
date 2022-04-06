/**
 * fileName       : SMSService
 * author         : kimjaejung
 * createDate     : 2022/03/12
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/12        kimjaejung       최초 생성
 *
 */
package com.map.mutual.side.auth.svc;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.JWTRefreshTokenLogEntity;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.model.dto.SMSAuthReqeustDto;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public interface AuthService {
    void sendMessageTest(String sendPhoneNum, String smsAuthNum) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, InvalidKeyException;
    void smsAuthNumSave(SMSAuthReqeustDto smsAuthReqeustDTO, String smsAuthNum);
    void smsAuthNumResponse(SMSAuthReqeustDto smsAuthResponseDTO);
    String makeAccessJWT(UserInfoDto user) throws Exception ;
    String makeRefreshJWT(String suid) throws Exception ;
    UserEntity findOneByPhone (String phone);
    String JWTAccessRefresh(String refreshToken) throws Exception;
    void saveJwtLog(JWTRefreshTokenLogEntity log) throws  Exception;
}
