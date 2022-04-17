/**
 * fileName       : SmsConstant
 * author         : kimjaejung
 * createDate     : 2022/03/12
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/12        kimjaejung       최초 생성
 *
 */
package com.map.mutual.side.auth.constant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.mutual.side.auth.model.dto.SmsDto;
import com.map.mutual.side.auth.utils.HttpSensClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Collections;

@Configuration
public class SMSService {
    private final Logger logger = LogManager.getLogger(SMSService.class);
    public static final String SENS_HOST_URL = "https://sens.apigw.ntruss.com";
    public static final String SENS_REQUEST_URL = "/sms/v2/services/";
    public static final String SENS_REQUEST_TYPE = "/messages";
    public static final String SENS_SVC_ID = "ncp:sms:kr:279058593171:sms-service";


    public static final String SENS_MESSAGE_TYPE_SMS = "SMS";
    public static final String SENS_MESSAGE_CONTENTTPYE_COMM = "COMM";
    public static final String SENS_MESSAGE_COUNTRYCODE_DEFAULT = "82";


    public static final String SENS_ACCESSKEY = "kNKPMYVwhTp3uIYbek9i";
    public static final String SENS_SECRETKEY = "XouxuOyjVhekRsLBqCEuocX9ghAugujpI4gvUlXD";


    public void sendMessageTest(String sendPhoneNum, String smsAuthNum) throws IOException {
        int resultCode = 0;


        String sensApiUrl = SENS_REQUEST_URL + SENS_SVC_ID + SENS_REQUEST_TYPE;
        String timeStamp = Long.toString(System.currentTimeMillis());
        String apiUrl = SENS_HOST_URL + sensApiUrl;

        SmsDto smsDto  = SmsDto.builder()
                .type(SENS_MESSAGE_TYPE_SMS)
                .contentType(SENS_MESSAGE_CONTENTTPYE_COMM)
                .countryCode(SENS_MESSAGE_COUNTRYCODE_DEFAULT)
                .from("01055967356")
//                .subject("SMS")
                .content("[인증]")
//                .content("기본 콘텐츠" + Integer.toString(RandomUtils.nextInt(10000, 100000)))
                .messages(Collections
                        .singletonList(SmsDto.MessageInfoDto.builder()
                                .to(sendPhoneNum)
                                .content("인증번호를 입력하세요 [" + smsAuthNum + "]")
                                .build()))
                .build();


        ObjectMapper mapper = new ObjectMapper();

        String jsonStr = mapper.writeValueAsString(smsDto);



        StringEntity stringEntity = new StringEntity(jsonStr, "UTF-8");

        try{
            HttpClient httpClient = HttpSensClient.getHttpClientInsecure();
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("x-ncp-apigw-timestamp", timeStamp);
            httpPost.addHeader("x-ncp-iam-access-key", SENS_ACCESSKEY);
            httpPost.addHeader("x-ncp-apigw-signature-v2", HttpSensClient.makeSignature(timeStamp, sensApiUrl));


            httpPost.setEntity(stringEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            resultCode = httpResponse.getStatusLine().getStatusCode();

        } catch (Exception e) {
            logger.error("Error : {}", e.getMessage());
        }
        logger.info(resultCode);
    }
    /**
     * Description : 미 가입자 YOPLE 초대 SMS 발송.
     * - sendPhoneNum - 타겟 핸드폰 번호
     * - userPhone - 초대자의 핸드폰 번호
     * - worldUserCode - 초대 월드의 초대자 월드 코드.
     * Name        :
     * Author      : 조 준 희
     * History     : [2022/04/17] - 조 준 희 - Create
     */
    public void inviteSendMessage(String sendPhoneNum, String userPhone, String worldUserCode) throws IOException {
        int resultCode = 0;

        String sensApiUrl = SENS_REQUEST_URL + SENS_SVC_ID + SENS_REQUEST_TYPE;
        String timeStamp = Long.toString(System.currentTimeMillis());
        String apiUrl = SENS_HOST_URL + sensApiUrl;

        SmsDto smsDto  = SmsDto.builder()
                .type(SENS_MESSAGE_TYPE_SMS)
                .contentType(SENS_MESSAGE_CONTENTTPYE_COMM)
                .countryCode(SENS_MESSAGE_COUNTRYCODE_DEFAULT)
                .from("01055967356")
//                .subject("SMS")
                .content("[인증]")
//                .content("기본 콘텐츠" + Integer.toString(RandomUtils.nextInt(10000, 100000)))
                .messages(Collections
                        .singletonList(SmsDto.MessageInfoDto.builder()
                                .to(sendPhoneNum)
                                .content("[YOPLE]\n" +
                                        userPhone + "님의 초대입니다.\n" +
                                        "월드 입장코드 [" + worldUserCode + "]")
                                .build()))
                .build();


        ObjectMapper mapper = new ObjectMapper();

        String jsonStr = mapper.writeValueAsString(smsDto);



        StringEntity stringEntity = new StringEntity(jsonStr, "UTF-8");

        try{
            HttpClient httpClient = HttpSensClient.getHttpClientInsecure();
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("x-ncp-apigw-timestamp", timeStamp);
            httpPost.addHeader("x-ncp-iam-access-key", SENS_ACCESSKEY);
            httpPost.addHeader("x-ncp-apigw-signature-v2", HttpSensClient.makeSignature(timeStamp, sensApiUrl));


            httpPost.setEntity(stringEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            resultCode = httpResponse.getStatusLine().getStatusCode();

        } catch (Exception e) {
            logger.error("Error : {}", e.getMessage());
        }
        logger.info(resultCode);
    }
}
