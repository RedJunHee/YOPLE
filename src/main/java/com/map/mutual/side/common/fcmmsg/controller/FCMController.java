package com.map.mutual.side.common.fcmmsg.controller;


import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.fcmmsg.constant.FCMConstant;
import com.map.mutual.side.common.fcmmsg.svc.FCMService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * fileName       : FCMController
 * author         : kimjaejung
 * createDate     : 2022/03/20
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/20        kimjaejung       최초 생성
 *
 */
@Log4j2
@RestController
@RequestMapping("/fcm")
public class FCMController {
    @Autowired
    private FCMService fcmService;

    @Autowired
    private UserInfoRepo userInfoRepo;

    @PostMapping("/generateToken")
    public ResponseEntity<ResponseJsonObject> generateToken(@RequestParam String token) {
        return fcmService.generateToken(token);
    }

    @PostMapping("/test")
    public ResponseEntity<ResponseJsonObject> tests(@RequestParam String token,
                                                    @RequestParam String title,
                                                    @RequestParam String body) {
        try {
            Notification notification = Notification.builder().setTitle(title).setBody(body).build();

            Message message = Message.builder()
                    .putData("data", "fcmMsgTest")
                    .setToken(token)
                    .setNotification(notification)
                    .build();
            String response = FirebaseMessaging.getInstance(FirebaseApp.getInstance(FCMConstant.FCM_INSTANCE)).send(message);
            log.info("Successfully sent message: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Error : {}", e.getErrorCode());
            throw new YOPLEServiceException(ApiStatusCode.USER_NOT_FOUND);
        }
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }


    @PostMapping("/sendNotification/topic")
    public void testTopic(@RequestParam FCMConstant.MSGType msgType,
                          @RequestParam Long worldId,
                          @RequestParam String userId) {
        CompletableFuture<FCMConstant.ResultType> response = fcmService.sendNotificationTopic(msgType, worldId, userId);
        response.thenAccept(d -> {
            if (d.getType().equals(FCMConstant.ResultType.SUCCESS.getType())) {
                log.info(d.getDesc());
            } else {
                log.error(d.getDesc());
            }
        });
    }

    @PostMapping("/sendNotification/token")
    public void testToken(@RequestParam FCMConstant.MSGType msgType,
                          @RequestParam String userId,
                          @RequestParam Long worldId) throws InterruptedException {
        String token = "d-fw6-17tkDokesl9fmT6q:APA91bGQUn4OT1b3reXhqEcdzb4UCRFdUkCadoxdWtsCTz9YOhMdlelQoss_Vnrl1GKEsuMB-AOPm9y_padkMaa8duVvKERddBfn_mDdP29VlV9sWUO27XvUkPX3636m7DBjQi-ynyEV";

        CompletableFuture<FCMConstant.ResultType> response = fcmService.sendNotificationToken(token, msgType, userId, worldId, null);
        response.thenAccept(d -> {
            if (d.getType().equals(FCMConstant.ResultType.SUCCESS.getType())) {
                log.info(d.getDesc());
            } else {
                log.error(d.getDesc());
            }
        });
    }
//
//
    @PostMapping("/subscribeTopic")
    public void testSubscribe(@RequestParam String token,
                          @RequestParam String topic) throws FirebaseMessagingException {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance(FirebaseApp.getInstance(FCMConstant.FCM_INSTANCE)).subscribeToTopic(Collections.singletonList(token), topic);
            if (!response.getErrors().isEmpty()) {
                throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED);
            }
        } catch (FirebaseMessagingException e) {
            throw e;
        }
    }
//    @PostMapping("/getTopicList")
//    public void getTopicList(@RequestParam String token) throws FirebaseMessagingException, IOException {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("https://iid.googleapis.com/iid/info/"+token+"?details=true")
//                .get()
//                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
//                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
//                .build();
//        FirebaseMessaging firebaseMessaging;
//        Response response = client.newCall(request).execute();
//        log.info(response.body().string());
//
//    }
//    public String getAccessToken() throws IOException {
//        String firebaseConfigPath = "fcm/fcm-yople-keys.json";
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
//                .createScoped(com.sun.tools.javac.util.List.of("https://www.googleapis.com/auth/cloud-platform"));
//        googleCredentials.refreshIfExpired();
//
//        log.info("ACCESS TOKEN : {}", googleCredentials.getAccessToken().getTokenValue());
//        return googleCredentials.getAccessToken().getTokenValue();
//    }


//====================REST API==================================
//    @GetMapping("/getToken")
//    public ResponseEntity<ResponseJsonObject> getToken() {
//
//        try {
//            fcmService.getAccessToken();
//        } catch (Exception e) {
//            log.error(e.getMessage());
//
//        }
//
//        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
//    }
//    @PostMapping("/sendMsg")
//    public ResponseEntity<ResponseJsonObject> sendMsg(@RequestParam String token) {
//
//        try {
//            fcmService.sendMessageTo(token,"테스트임니다ㅋㅋ","restAPiTest");
//        } catch (Exception e) {
//            log.error(e.getMessage());
//
//        }
//
//        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
//    }

}
