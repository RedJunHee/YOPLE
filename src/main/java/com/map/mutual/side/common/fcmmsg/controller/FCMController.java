package com.map.mutual.side.common.fcmmsg.controller;


import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.fcmmsg.svc.FCMService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    public ResponseEntity<ResponseJsonObject> generateToken(@RequestBody Map<String, String> token) throws YOPLEServiceException {
        return fcmService.generateToken(token.get("token"));
    }



    @PostMapping("/test")
    public void tests(@RequestBody  UserInfoDto info) throws YOPLEServiceException {
        log.info(info.getName());
        log.info(info.getProfileUrl());
    }


}
