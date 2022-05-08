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
import com.map.mutual.side.common.utils.CryptUtils;
import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.repository.WorldRepo;
import com.map.mutual.side.world.repository.WorldUserMappingRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
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

    @Autowired
    private WorldRepo worldRepo;

    public ResponseEntity<ResponseJsonObject> generateToken(String token) throws YOPLEServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
        UserEntity userEntity;
        try {
            userEntity = userInfoRepo.findBySuid(userInfoDto.getSuid());
        } catch (Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
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

    private void registryFcmToken(UserEntity userEntity, String token) throws YOPLEServiceException {
        List<FcmTopicEntity> fcmTopicEntities = new ArrayList<>();
        try {

            userEntity.setFcmToken(token);
            userInfoRepo.save(userEntity);

            List<WorldUserMappingEntity> worldUserMappingEntities = worldUserMappingRepo.findByUserSuid(CryptUtils.AES_Decode(userEntity.getSuid()));
            if (!worldUserMappingEntities.isEmpty()) {
                worldUserMappingEntities.forEach(data -> {
                    try {
                        TopicManagementResponse response = FirebaseMessaging.getInstance(FirebaseApp.getInstance(FCMConstant.FCM_INSTANCE)).subscribeToTopic(Collections.singletonList(token), String.valueOf(data.getWorldId()));
                        log.info(response);
                    } catch (FirebaseMessagingException e) {
                        try {
                            throw new YOPLEServiceException(ApiStatusCode.REGISTRY_FCM_TOPIC_FAIL);
                        } catch (YOPLEServiceException ex) {
                            log.error(ex.getMessage());
                        }
                    }
                    FcmTopicEntity fcmTopicEntity = FcmTopicEntity.builder().fcmToken(token).worldId(data.getWorldId()).build();
                    fcmTopicEntities.add(fcmTopicEntity);
                });
                fcmTopicRepository.saveAll(fcmTopicEntities);
            }
        } catch (Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
    }

    public void deleteFcmToken(UserInfoDto userInfoDto) throws YOPLEServiceException {
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
                        try {
                            throw new YOPLEServiceException(ApiStatusCode.UNSUBSCRIPTION_FCM_TOPIC_FAIL);
                        } catch (YOPLEServiceException ex) {
                            log.error(ex.getMessage());
                        }
                    }
                });
                fcmTopicRepository.deleteAll(fcmTopicEntities);
                userInfoRepo.save(userEntity);
            }
        } catch (Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
    }

    @Async(value = "YOPLE-Executor")
    public CompletableFuture<FCMConstant.ResultType> sendNotificationToken(String targetFcmToken, FCMConstant.MSGType msgType, String userSuid, Long worldId, Long reviewId) throws InterruptedException, YOPLEServiceException {
        String body;
        Map<String, String> msgData = new HashMap<>();
        try {
            switch (msgType) {
                case A:
                    String aUserId = userInfoRepo.findBySuid(userSuid).getUserId();
                    String aWorldName = worldRepo.findByWorldId(worldId).getWorldName();
                    body = aUserId
                            + "님이 "
                            + aWorldName
                            + "에 회원님을 초대하였습니다.";
                    msgData.put("worldId", String.valueOf(worldId));
                    msgData.put("userSuid", CryptUtils.AES_Encode(userSuid));
                    break;
                case C:
                    String cUserId = userInfoRepo.findBySuid(userSuid).getUserId();
                    String cWorldName = worldRepo.findByWorldId(worldId).getWorldName();
                    body = cWorldName
                            + "에서"
                            + cUserId
                            + " 님이 내 리뷰에 반응을 남겼습니다.";
                    msgData.put("worldId", String.valueOf(worldId));
                    msgData.put("userSuid", CryptUtils.AES_Encode(userSuid));
                    msgData.put("reviewId", String.valueOf(reviewId));


                    break;
                default:
                    log.error("[FCM]잘못된 알림 타입 입니다.");
                    return CompletableFuture.completedFuture(FCMConstant.ResultType.FAIL);
            }
        } catch (Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }


        Notification notification = Notification.builder()
                .setTitle(FCMConstant.YOPLE)
                .setBody(body)
                .build();

        Message message = Message.builder()
                    .setToken(targetFcmToken)
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
    public CompletableFuture<FCMConstant.ResultType> sendNotificationTopic(FCMConstant.MSGType msgType, Long worldId, String userSuid) throws YOPLEServiceException {
        String body;
        String decodedSuid;
        try {
            decodedSuid = CryptUtils.AES_Decode(userSuid);
        } catch (Exception e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
        Map<String, String> msgData = new HashMap<>();
        switch (msgType) {
            case B:
                String userId = userInfoRepo.findBySuid(decodedSuid).getUserId();
                String worldName  = worldRepo.findByWorldId(worldId).getWorldName();
                body = worldName
                        + "에 "
                        + userId
                        + "님이 초대되었습니다.";
                msgData.put("worldId", String.valueOf(worldId));
                msgData.put("userSuid", userSuid);
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
                    .setTopic(String.valueOf(worldId))
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
