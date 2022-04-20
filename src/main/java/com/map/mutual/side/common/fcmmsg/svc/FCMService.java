package com.map.mutual.side.common.fcmmsg.svc;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.fcmmsg.constant.FCMConstant;
import com.map.mutual.side.common.fcmmsg.model.entity.FcmTopicEntity;
import com.map.mutual.side.common.fcmmsg.repository.FcmTopicRepository;
import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.repository.WorldUserMappingRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * fileName       : FCMService
 * author         : kimjaejung
 * createDate     : 2022/03/20
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/20        kimjaejung       최초 생성
 */
@Component
@Log4j2
public class FCMService {
    @Autowired
    private FcmTopicRepository fcmTopicRepository;
    @Autowired
    private WorldUserMappingRepo worldUserMappingRepo;
    @Autowired
    private UserInfoRepo userInfoRepo;

    public ResponseEntity<ResponseJsonObject> generateToken(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        UserEntity userEntity = userInfoRepo.findBySuid(userInfoDto.getSuid());
        if (userEntity.getFcmToken() == null) {
            userEntity.setFcmToken(token);
            userInfoRepo.save(userEntity);
            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
        } else if (userEntity.getFcmToken().equals(token)) {
            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
        } else if (userEntity.getFcmToken().equals(FCMConstant.EXPIRED)) {
            registryFcmToken(userEntity, token);
            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
        } else if (!userEntity.getFcmToken().equals(token)) {
            List<FcmTopicEntity> fcmTopicEntity = fcmTopicRepository.findAllByFcmToken(userEntity.getFcmToken());
            fcmTopicRepository.deleteAll(fcmTopicEntity);
            registryFcmToken(userEntity, token);
            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
        } else
            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void registryFcmToken(UserEntity userEntity, String token) {
        List<FcmTopicEntity> fcmTopicEntities = new ArrayList<>();
        try {

            userEntity.setFcmToken(token);
            userInfoRepo.save(userEntity);

            List<WorldUserMappingEntity> worldUserMappingEntities = worldUserMappingRepo.findByUserSuid(userEntity.getSuid());
            if (!worldUserMappingEntities.isEmpty()) {
                worldUserMappingEntities.forEach(data -> {
                    try {
                        TopicManagementResponse response = FirebaseMessaging.getInstance(FirebaseApp.getInstance(FCMConstant.FCM_INSTANCE)).subscribeToTopic(Collections.singletonList(token), String.valueOf(data.getWorldId()));
                        log.info(response);
                    } catch (FirebaseMessagingException e) {
                        throw new YOPLEServiceException(ApiStatusCode.REGISTRY_FCM_TOPIC_FAIL);
                    }
                    FcmTopicEntity fcmTopicEntity = FcmTopicEntity.builder().fcmToken(token).worldId(data.getWorldId()).build();
                    fcmTopicEntities.add(fcmTopicEntity);
                });
                fcmTopicRepository.saveAll(fcmTopicEntities);
            }
        } catch (YOPLEServiceException e) {
            throw e;
        }
    }

    public void deleteFcmToken(UserInfoDto userInfoDto) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
        List<FcmTopicEntity> fcmTopicEntities;

        try {
            UserEntity userEntity = userInfoRepo.findBySuid(userInfoDto.getSuid());

            if (userEntity.getFcmToken() != null) {
                userEntity.setFcmToken(FCMConstant.EXPIRED);
                fcmTopicEntities = fcmTopicRepository.findAllByFcmToken(userEntity.getFcmToken());

                fcmTopicEntities.forEach(data -> {
                    try {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(Collections.singletonList(data.getFcmToken()), String.valueOf(data.getWorldId()));
                    } catch (FirebaseMessagingException e) {
                        throw new YOPLEServiceException(ApiStatusCode.UNSUBSCRIPTION_FCM_TOPIC_FAIL);
                    }
                });
                fcmTopicRepository.deleteAll(fcmTopicEntities);
                userInfoRepo.save(userEntity);
            }
        } catch (YOPLEServiceException e) {
            throw e;
        }
    }

    @Async(value = "YOPLE-Executor")
    public CompletableFuture<FCMConstant.ResultType> sendNotificationToken(String fcmToken, FCMConstant.MSGType msgType, String userId, String worldName, Map<String, String> msgData) throws InterruptedException {
        String body = "";
        switch (msgType) {
            case A:
                body = userId
                        + "님이 "
                        + worldName
                        + "에 회원님을 초대하였습니다.";
                break;
            case C:
                body = worldName
                        + "에서"
                        + userId
                        + " 님이 내 리뷰에 반응을 남겼습니다.";
                break;
            default:
                log.error("[FCM]잘못된 알림 타입 입니다.");
                return CompletableFuture.completedFuture(FCMConstant.ResultType.FAIL);
        }

        Notification notification = Notification.builder()
                .setTitle(FCMConstant.YOPLE)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .putAllData(msgData)
                .build();
        try {
            FirebaseMessaging.getInstance(FirebaseApp.getInstance(FCMConstant.FCM_INSTANCE)).send(message);
        } catch (FirebaseMessagingException e) {
            return CompletableFuture.completedFuture(FCMConstant.ResultType.FAIL);
        }
        return CompletableFuture.completedFuture(FCMConstant.ResultType.SUCCESS);
    }

    @Async(value = "YOPLE-Executor")
    public void sendNotificationTopic(FCMConstant.MSGType msgType, String topic, String userId, String worldName, Map<String, String> msgData) {
        String body;
        switch (msgType) {
            case B:
                body = worldName
                        + "에 "
                        + userId
                        + "님이 초대되었습니다.";
                break;
            default:
                throw new YOPLEServiceException(ApiStatusCode.FCM_NOTIFICATION_TYPE_INVALID, "[FCM]잘못된 메세지 타입입니다.");
        }

        Notification notification = Notification.builder()
                .setTitle(FCMConstant.YOPLE)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(notification)
                .putAllData(msgData)
                .build();

        try {
            FirebaseMessaging.getInstance(FirebaseApp.getInstance(FCMConstant.FCM_INSTANCE)).send(message);

        } catch (FirebaseMessagingException e) {
            throw new YOPLEServiceException(ApiStatusCode.SEND_FCM_NOTIFICATION_FAIL);
        }
    }

//    private void updateFcmToken(String userSuid, String newToken)  {
//        try {
//            FcmTokenEntity fcmTokenEntity = fcmTokenRepository.findByUserSuid(userSuid);
//            fcmTokenEntity.setFcmToken(newToken);
//            fcmTokenRepository.save(fcmTokenEntity);

//            List<FcmTopicEntity> fcmTopicEntities = fcmTopicRepository.findAllByFcmToken(oldToken);
//
//            if(!fcmTopicEntities.isEmpty()) {
//                fcmTopicEntities.forEach(data -> {data.setFcmToken(newToken);});
//                fcmTopicRepository.saveAll(fcmTopicEntities);
//            }
//            log.info("Update To Registry Token");
//        } catch (YOPLEServiceException e) {
//            throw e;
//        }
//    }
// =====================================================================================================================

    //FCM MESSAGE REST API
//    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
//        String message = makeMessage(targetToken, title, body);
//
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
//        Request request = new Request.Builder()
//                .url(API_URL)
//                .post(requestBody)
//                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
//                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
//                .build();
//
//        Response response = client.newCall(request).execute();
//
//        log.info(response.body().string());
//    }
//
//    // 파라미터를 FCM이 요구하는 body 형태로 만들어준다.
//    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
//        FCMMessageDto fcmMessage = FCMMessageDto.builder()
//                .message(FCMMessageDto.Message.builder()
//                        .token(targetToken)
//                        .notification(FCMMessageDto.Notification.builder()
//                                .title(title)
//                                .body(body)
//                                .image(null)
//                                .build()
//                        )
//                        .build()
//                )
//                .validate_only(false)
//                .build();
//        return objectMapper.writeValueAsString(fcmMessage);
//    }
//
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
}
