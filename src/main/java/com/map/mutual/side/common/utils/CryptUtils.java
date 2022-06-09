package com.map.mutual.side.common.utils;

import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * {@Summary en&de-crypt, 암호화 util 클래스 }
 * Author      : 조 준 희
 * History     : [2022-01-14]
 */
@Component
public class CryptUtils {
    private final static Logger logger = LogManager.getLogger(CryptUtils.class);

    private static String secretKey ;
    static String IV = ""; // 16bit

    public CryptUtils(@Value("${aes-secret}") String secretKey) {
        this.secretKey = secretKey;
        IV = secretKey.substring(0, 16);
    }

    public String getSecretKey() {
        return secretKey;
    }

    /**
     *  문자열을 Base64로 인코딩
     * @param input
     * @return
     */
    public static String Base64Encoding(String input)
    {
        byte[] targetBytes = input.getBytes(); // Base64 인코딩 ///////////////////////////////////////////////////
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(targetBytes);
        return new String(encodedBytes);
    }

    /**
     * Base64로 인코딩된 문자열을 Base64디코딩
     * @param input
     * @return
     */
    public static String Base64Decoding(String input){
        byte[] targetBytes = input.getBytes();
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(targetBytes);
        return new String(decodedBytes);
    }


    /**
     * 평문을 AES256으로 암호화하여 Base64인코딩 합니다.
     * @param text 평문
     * @return 평문을 AES256으로 암호화 후 Base64인코딩한 문자열
     * @throws Exception
     */
    public static String AES_Encode(String text) throws YOPLEServiceException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

            byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
            String result = Base64.getEncoder().encodeToString(encrypted);

            //logger.debug("AES Encode  { {} => {} }",text, result);

            return result;
        }catch(Exception e){
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR, "암호 변조된 값이 존재합니다. 보안 위험.");
        }
    }

    /**
     * AES256으로 암호화된 문자열을 AES256으로 복호화하여 Base64인코딩 합니다.
     * @param cipherText 암호화된 문자열
     * @return 복호화된 평문을 Base64인코딩한 문자열
     * @throws Exception
     */
    public static String AES_Decode(String cipherText) throws YOPLEServiceException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);

            String result = new String(decrypted, "UTF-8");

            //logger.debug("AES Decode  { {} => {} }",cipherText, result);

            return result;
        }catch(Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED, "암호 변조된 값이 존재합니다. 보안 위험.");
        }
    }
}
