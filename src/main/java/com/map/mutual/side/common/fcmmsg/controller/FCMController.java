package com.map.mutual.side.common.fcmmsg.controller;


import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.fcmmsg.constant.FCMConstant;
import com.map.mutual.side.common.fcmmsg.svc.FCMService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

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

    @PostMapping("/generate")
    public ResponseEntity<ResponseJsonObject> generateToken(@RequestBody Map<String, String> token) throws YOPLEServiceException, ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
        String suid = userInfoDto.getSuid();
        fcmService.generateToken(suid, token.get("token"));
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }









    // TODO: 2022/05/31 삭제 -- TEST AREA ----
    @PostMapping("/test")
    public ResponseEntity<ResponseJsonObject> tests(@RequestBody  UserInfoDto info)  {
        log.info(info.getName());
        log.info(info.getProfileUrl());
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(info), HttpStatus.OK);
    }
    @PostMapping("/testP")
    public ResponseEntity<ResponseJsonObject> teststs(@RequestParam  String params)  {
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);

    }

    @PostMapping("/testT")
    public ResponseEntity<ResponseJsonObject> tests(@RequestParam String token,
                                                    @RequestParam String title,
                                                    @RequestParam String body) throws YOPLEServiceException {
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


}
