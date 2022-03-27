package com.map.mutual.side.message.controller;


import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.message.svc.FCMService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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


    @GetMapping("/getToken")
    public ResponseEntity<ResponseJsonObject> getToken() {

        try {
            fcmService.getAccessToken();
        } catch (Exception e) {
            log.error(e.getMessage());

        }

        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }
    @PostMapping("/sendMsg")
    public ResponseEntity<ResponseJsonObject> sendMsg(@RequestParam String token) {

        try {
            fcmService.sendMessageTo(token,"test","test");
        } catch (Exception e) {
            log.error(e.getMessage());

        }

        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }
    @PostMapping("/tests")
    public void tests() throws IOException, FirebaseMessagingException {

        Message message = Message.builder()
                .putData("score", "850")
                .putData("time", "2:45")
                .setToken("esBfgt5HiUneryjd8u0DqQ:APA91bER7wFWAKoLPqhzNTVOjnrcI7B0vq9g2LA0CIVviHJ1a7nU95y7i6ri1Jwjg_QoZ0u9zp0HJ5wOEGktRJUkBBLqtk3q8eTMUmujDwMH4nQ6aSnpBjIzzrEveaO2AlReSRouMAgW")
                .build();
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent message: " + response);
    }
}
