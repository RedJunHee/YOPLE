package com.map.mutual.side.common.fcmmsg.svc;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.common.entity.ApiLog;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.exception.YOPLETransactionException;
import com.map.mutual.side.common.fcmmsg.constant.FCMConstant;
import com.map.mutual.side.common.fcmmsg.model.entity.FcmTopicEntity;
import com.map.mutual.side.common.fcmmsg.repository.FcmTopicRepository;
import com.map.mutual.side.common.repository.LogRepository;
import com.map.mutual.side.common.utils.CryptUtils;
import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.repository.WorldRepo;
import com.map.mutual.side.world.repository.WorldUserMappingRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    @Autowired
    private LogRepository logRepository;

    @Async
    public void generateToken(String suid, String token) throws YOPLEServiceException, ExecutionException, InterruptedException {
        long executeTimer;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        CompletableFuture<Boolean> registry = new CompletableFuture<>();

        UserEntity userEntity = userInfoRepo.findBySuid(suid);

        if (userEntity.getFcmToken() == null) {
            userEntity.setFcmToken(token);
            userInfoRepo.save(userEntity);
            registry.complete(true);
        } else if (userEntity.getFcmToken().equals(token)) {
            return;
        } else if (userEntity.getFcmToken().equals(FCMConstant.EXPIRED)) {
            registry =  CompletableFuture.supplyAsync(() -> {
                boolean result;
                try {
                    result =  registryFcmToken(userEntity, token);
                } catch (YOPLEServiceException e) {
                    return false;
                }
                return result;
            });

        } else if (!userEntity.getFcmToken().equals(token)) {
            List<FcmTopicEntity> fcmTopicEntity = fcmTopicRepository.findAllByFcmToken(userEntity.getFcmToken());
            fcmTopicRepository.deleteAll(fcmTopicEntity);
             registry = CompletableFuture.supplyAsync(() -> {
                boolean result;
                try {
                    result =  registryFcmToken(userEntity, token);
                } catch (YOPLEServiceException e) {
                    return false;
                }
                return result;
            });
        } else {
            stopWatch.stop();
            executeTimer = stopWatch.getTotalTimeMillis();
            ApiLog apiLog = ApiLog.builder()
                    .suid(suid)
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[FCM]Fail To Generate FCM Token : " + suid)
                    .apiStatus('N')
                    .processTime((float) (executeTimer * 0.001))
                    .build();
            logRepository.save(apiLog);
            throw new YOPLEServiceException(ApiStatusCode.GENERATE_FAILED_TO_TOKEN);
        }
        stopWatch.stop();
        executeTimer = stopWatch.getTotalTimeMillis();
        if(registry.get()) {
            ApiLog apiLog = ApiLog.builder()
                    .suid(suid)
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[FCM]Success To Generate FCM Token : " + suid)
                    .apiStatus('Y')
                    .processTime((float) (executeTimer * 0.001))
                    .build();
            logRepository.save(apiLog);
        } else {
            ApiLog apiLog = ApiLog.builder()
                    .suid(suid)
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[FCM]Fail To Generate FCM Token : " + suid)
                    .apiStatus('N')
                    .processTime((float) (executeTimer * 0.001))
                    .build();
            logRepository.save(apiLog);
            throw new YOPLEServiceException(ApiStatusCode.GENERATE_FAILED_TO_TOKEN);
        }
    }

    private boolean registryFcmToken(UserEntity userEntity, String token) throws YOPLEServiceException {
        List<FcmTopicEntity> fcmTopicEntities = new ArrayList<>();
        try {

            userEntity.setFcmToken(token);
            userInfoRepo.save(userEntity);

            List<WorldUserMappingEntity> worldUserMappingEntities = worldUserMappingRepo.findByUserSuid(userEntity.getSuid());
            if (!worldUserMappingEntities.isEmpty()) {
                worldUserMappingEntities.forEach(data -> {
                    try {
                        FirebaseMessaging.getInstance(FirebaseApp.getInstance(FCMConstant.FCM_INSTANCE)).subscribeToTopic(Collections.singletonList(token), String.valueOf(data.getWorldId()));
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
        return true;
    }

    @Async(value = "YOPLE-Executor")
    public void deleteFcmToken(String userSuid) throws YOPLEServiceException {
        long executeTimer;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<FcmTopicEntity> fcmTopicEntities;

        UserEntity userEntity = userInfoRepo.findBySuid(userSuid);

        if (userEntity.getFcmToken() != null) {
            userEntity.setFcmToken(FCMConstant.EXPIRED);
            fcmTopicEntities = fcmTopicRepository.findAllByFcmToken(userEntity.getFcmToken());

            fcmTopicRepository.deleteAll(fcmTopicEntities);
            userInfoRepo.save(userEntity);

            fcmTopicEntities.forEach(data -> {
                try {
                    FirebaseMessaging.getInstance(FirebaseApp.getInstance(FCMConstant.FCM_INSTANCE)).unsubscribeFromTopic(Collections.singletonList(data.getFcmToken()), String.valueOf(data.getWorldId()));
                } catch (FirebaseMessagingException e) {
                    stopWatch.stop();
                    ApiLog apiLog = ApiLog.builder()
                            .suid(userSuid)
                            .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                            .apiDesc("[FCM]Fail To Delete Fcm Token : " + userSuid)
                            .apiStatus('N')
                            .processTime((float) (stopWatch.getTotalTimeMillis() * 0.001))
                            .build();
                    logRepository.save(apiLog);
                    throw new YOPLETransactionException(ApiStatusCode.FAIL_DELETE_FCM_TOKEN);
                }
            });

            stopWatch.stop();
            executeTimer = stopWatch.getTotalTimeMillis();
            ApiLog apiLog = ApiLog.builder()
                    .suid(userSuid)
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[FCM]Success To Delete Fcm Token : " + userSuid)
                    .apiStatus('Y')
                    .processTime((float) (executeTimer * 0.001))
                    .build();
            logRepository.save(apiLog);

        }
    }

    @Async(value = "YOPLE-Executor")
    public void sendNotificationToken(String targetFcmToken, FCMConstant.MSGType msgType, String userSuid, Long worldId, Long reviewId) throws YOPLEServiceException {
        StopWatch stopWatch = new StopWatch();
        long executeTimer;

        String body = "";
        Map<String, String> msgData = new HashMap<>();
        stopWatch.start();
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
                stopWatch.stop();
                executeTimer = stopWatch.getTotalTimeMillis();
                ApiLog apiLog = ApiLog.builder()
                        .suid(userSuid)
                        .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                        .apiDesc("[FCM]Fail to Send Token : " + targetFcmToken)
                        .apiStatus('N')
                        .processTime((float) (executeTimer * 0.001))
                        .build();
                logRepository.save(apiLog);
                throw new YOPLETransactionException(ApiStatusCode.SEND_TO_FCM_FAILED);
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

            stopWatch.stop();
            executeTimer = stopWatch.getTotalTimeMillis();
            ApiLog apiLog = ApiLog.builder()
                    .suid(userSuid)
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[FCM]Success to Send Token : " + targetFcmToken)
                    .apiStatus('Y')
                    .processTime((float) (executeTimer * 0.001))
                    .build();
            logRepository.save(apiLog);
        } catch (FirebaseMessagingException e) {
            stopWatch.stop();
            executeTimer = stopWatch.getTotalTimeMillis();
            ApiLog apiLog = ApiLog.builder()
                    .suid(userSuid)
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[FCM]Fail to Send Token : " + targetFcmToken)
                    .apiStatus('N')
                    .processTime((float) (executeTimer * 0.001))
                    .build();
            logRepository.save(apiLog);
            throw new YOPLETransactionException(ApiStatusCode.SEND_TO_FCM_FAILED);
        }
    }

    public Boolean sendNotificationTopic(FCMConstant.MSGType msgType, Long worldId, String userSuid) throws YOPLEServiceException {
        StopWatch stopWatch = new StopWatch();
        long executeTimer;

        String body;
        Map<String, String> msgData = new HashMap<>();

        stopWatch.start();
        switch (msgType) {
            case B:
                String userId = userInfoRepo.findBySuid(userSuid).getUserId();
                String worldName = worldRepo.findByWorldId(worldId).getWorldName();
                body = worldName
                        + "에 "
                        + userId
                        + "님이 초대되었습니다.";
                msgData.put("worldId", String.valueOf(worldId));
                msgData.put("userSuid", userSuid);
                break;
            default:
                stopWatch.stop();
                executeTimer = stopWatch.getTotalTimeMillis();
                ApiLog apiLog = ApiLog.builder()
                        .suid("")
                        .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                        .apiDesc("[FCM]Fail to Send Topic : " + worldId)
                        .apiStatus('N')
                        .processTime((float) (executeTimer * 0.001))
                        .build();
                logRepository.save(apiLog);
                throw new YOPLEServiceException(ApiStatusCode.SEND_TO_FCM_FAILED);
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

            stopWatch.stop();
            executeTimer = stopWatch.getTotalTimeMillis();
            ApiLog apiLog = ApiLog.builder()
                    .suid("")
                    .apiName(Thread.currentThread().getStackTrace()[1].getMethodName())
                    .apiDesc("[FCM]Success to Send Topic : " + worldId)
                    .apiStatus('Y')
                    .processTime((float) (executeTimer * 0.001))
                    .build();
            logRepository.save(apiLog);

        } catch (FirebaseMessagingException e) {
            throw new YOPLEServiceException(ApiStatusCode.SEND_TO_FCM_FAILED, e.getMessage());
        }
        return true;
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
