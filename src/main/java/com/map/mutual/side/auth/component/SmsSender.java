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
package com.map.mutual.side.auth.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.mutual.side.auth.model.dto.SmsDto;
import com.map.mutual.side.auth.utils.HttpSensClient;
import com.map.mutual.side.common.entity.ApiLog;
import com.map.mutual.side.common.repository.LogRepository;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;

@Component
public class SmsSender {
    private final Logger logger = LogManager.getLogger(SmsSender.class);
    private final String SENS_HOST_URL;
    private final String SENS_REQUEST_URL ;
    private final String SENS_REQUEST_TYPE ;
    private final String SENS_SVC_ID ;
    private final String SENS_MESSAGE_TYPE_SMS;
    private final String SENS_MESSAGE_CONTENTTPYE_COMM ;
    private final String SENS_MESSAGE_COUNTRYCODE_DEFAULT ;
    private final String SENS_ACCESSKEY ;
    private final String SENS_SECRETKEY ;

    private LogRepository logRepository;

    @Autowired
    public SmsSender(@Value("${yople.sens.host}") String SENS_HOST_URL
            ,@Value("${yople.sens.url}") String SENS_REQUEST_URL
            ,@Value("${yople.sens.type}") String SENS_REQUEST_TYPE
            ,@Value("${yople.sens.svc_id}") String SENS_SVC_ID
            ,@Value("${yople.sens.message.type}") String SENS_MESSAGE_TYPE_SMS
            ,@Value("${yople.sens.message.content_type}") String SENS_MESSAGE_CONTENTTPYE_COMM
            ,@Value("${yople.sens.message.country_code}") String SENS_MESSAGE_COUNTRYCODE_DEFAULT
            ,@Value("${yople.sens.accessKey}") String SENS_ACCESSKEY
            ,@Value("${yople.sens.secretKey}") String SENS_SECRETKEY
            , LogRepository logRepository) {
        this.SENS_HOST_URL = SENS_HOST_URL;
        this.SENS_REQUEST_URL = SENS_REQUEST_URL;
        this.SENS_REQUEST_TYPE = SENS_REQUEST_TYPE;
        this.SENS_SVC_ID = SENS_SVC_ID;
        this.SENS_MESSAGE_TYPE_SMS = SENS_MESSAGE_TYPE_SMS;
        this.SENS_MESSAGE_CONTENTTPYE_COMM = SENS_MESSAGE_CONTENTTPYE_COMM;
        this.SENS_MESSAGE_COUNTRYCODE_DEFAULT = SENS_MESSAGE_COUNTRYCODE_DEFAULT;
        this.SENS_ACCESSKEY = SENS_ACCESSKEY;
        this.SENS_SECRETKEY = SENS_SECRETKEY;
        this.logRepository = logRepository;
    }

    public void sendAuthMessage(String sendPhoneNum, String smsAuthNum) throws IOException {
        int resultCode = 0;
        long executeTimer;
        StopWatch stopWatch = new StopWatch();


        String sensApiUrl = SENS_REQUEST_URL + SENS_SVC_ID + SENS_REQUEST_TYPE;
        String timeStamp = Long.toString(System.currentTimeMillis());
        String apiUrl = SENS_HOST_URL + SENS_REQUEST_URL + SENS_SVC_ID + SENS_REQUEST_TYPE;

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
            stopWatch.start();
            HttpClient httpClient = HttpSensClient.getHttpClientInsecure();
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("x-ncp-apigw-timestamp", timeStamp);
            httpPost.addHeader("x-ncp-iam-access-key", SENS_ACCESSKEY);
            httpPost.addHeader("x-ncp-apigw-signature-v2", makeSignature(timeStamp, sensApiUrl));


            httpPost.setEntity(stringEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            resultCode = httpResponse.getStatusLine().getStatusCode();

        } catch (Exception e) {
            stopWatch.stop();
            executeTimer = stopWatch.getTotalTimeMillis();
            ApiLog apiLog = ApiLog.builder()
                    .suid("")
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[SEND]Fail to "+sendPhoneNum+" [REASON]"+e.getMessage())
                    .apiStatus('N')
                    .processTime(executeTimer)
                    .build();
            logRepository.save(apiLog);
            logger.error("SMS 전송 Error - {}에 전송을 실패 : {}", sendPhoneNum, e.getMessage());
        }

        stopWatch.stop();
        executeTimer = stopWatch.getTotalTimeMillis();
        ApiLog apiLog;
        if (resultCode == 202) {
            apiLog = ApiLog.builder()
                    .suid("")
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[SEND]Success to " + sendPhoneNum)
                    .apiStatus('Y')
                    .processTime((float) (executeTimer * 0.001))
                    .build();
            logRepository.save(apiLog);
            logger.debug("SMS 전송 Success - {}에 전송 성공", sendPhoneNum);

        } else {
            apiLog = ApiLog.builder()
                    .suid("")
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[SEND]Fail to " + sendPhoneNum + " [ResultCode] : " + resultCode)
                    .apiStatus('N')
                    .processTime(executeTimer)
                    .build();
            logRepository.save(apiLog);
            logger.error("SMS 전송 Error - {}에 전송을 실패. ResultCode : {}", sendPhoneNum, resultCode);
        }
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
    @Async(value = "YOPLE-Executor")
    public void inviteSendMessage(String sendPhoneNum, String userPhone, String worldUserCode) throws IOException {
        int resultCode = 0;
        long executeTimer;
        StopWatch stopWatch = new StopWatch();

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
            stopWatch.start();
            HttpClient httpClient = HttpSensClient.getHttpClientInsecure();
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("x-ncp-apigw-timestamp", timeStamp);
            httpPost.addHeader("x-ncp-iam-access-key", SENS_ACCESSKEY);
            httpPost.addHeader("x-ncp-apigw-signature-v2", makeSignature(timeStamp, sensApiUrl));


            httpPost.setEntity(stringEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            resultCode = httpResponse.getStatusLine().getStatusCode();

        } catch (Exception e) {
            stopWatch.stop();
            executeTimer = stopWatch.getTotalTimeMillis();
            ApiLog apiLog = ApiLog.builder()
                    .suid("")
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[SEND]Fail to "+sendPhoneNum+" [REASON]"+e.getMessage())
                    .apiStatus('N')
                    .processTime(executeTimer)
                    .build();
            logRepository.save(apiLog);
            logger.error("SMS 전송 Error - {}에 전송을 실패 : {}", sendPhoneNum, e.getMessage());
        }

        stopWatch.stop();
        executeTimer = stopWatch.getTotalTimeMillis();
        ApiLog apiLog;
        if (resultCode == 202) {
            apiLog = ApiLog.builder()
                    .suid("")
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[SEND]Success to " + sendPhoneNum)
                    .apiStatus('Y')
                    .processTime((float) (executeTimer * 0.001))
                    .build();
            logRepository.save(apiLog);
            logger.debug("SMS 전송 Success - {}에 전송 성공", sendPhoneNum);

        } else {
            apiLog = ApiLog.builder()
                    .suid("")
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[SEND]Fail to " + sendPhoneNum + " [ResultCode] : " + resultCode)
                    .apiStatus('N')
                    .processTime(executeTimer)
                    .build();
            logRepository.save(apiLog);
            logger.error("SMS 전송 Error - {}에 전송을 실패. ResultCode : {}", sendPhoneNum, resultCode);
        }
    }

    private String makeSignature(String timestamp, String sensApiUrl) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(sensApiUrl)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(SENS_ACCESSKEY)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(SENS_SECRETKEY.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);

        return encodeBase64String;
    }

}
