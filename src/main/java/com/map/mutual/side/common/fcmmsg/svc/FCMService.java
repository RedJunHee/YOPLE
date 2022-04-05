package com.map.mutual.side.common.fcmmsg.svc;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.fcmmsg.FCMConstant;
import com.map.mutual.side.common.fcmmsg.model.entity.FcmTopicEntity;
import com.map.mutual.side.common.fcmmsg.repository.FcmTopicRepository;
import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.repository.WorldUserMappingRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * fileName       : FCMService
 * author         : kimjaejung
 * createDate     : 2022/03/20
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/20        kimjaejung       최초 생성
 *
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




    public void generateToken(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        try {
            UserEntity userEntity = userInfoRepo.findBySuid(userInfoDto.getSuid());

            if (userEntity.getFcmToken().equals(token)) {
                return;
            } else if (userEntity.getFcmToken() == null) {
                userEntity.setFcmToken(token);
                userInfoRepo.save(userEntity);
            } else {
                registryFcmToken(token);
            }
        } catch (Exception e) {
            throw e;
        }

    }



    public void registryFcmToken(String token)  {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            UserEntity userEntity = userInfoRepo.findBySuid(userInfoDto.getSuid());
            userEntity.setFcmToken(token);

            if (userEntity.getFcmToken() == null) {
                userInfoRepo.save(userEntity);
            } else if (!userEntity.getFcmToken().equals(token)) {
                List<FcmTopicEntity> fcmTopicEntities = new ArrayList<>();

                List<WorldUserMappingEntity> worldUserMappingEntities = worldUserMappingRepo.findByUserSuid(userInfoDto.getSuid());
                if(!worldUserMappingEntities.isEmpty()) {
                    worldUserMappingEntities.forEach(data -> {
                        try {
                            TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(Collections.singletonList(token), String.valueOf(data.getWorldId()));
                            log.info(response);
                        } catch (FirebaseMessagingException e) {
                            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
                        }
                        FcmTopicEntity fcmTopicEntity = FcmTopicEntity.builder().fcmToken(token).worldId(data.getWorldId()).build();
                        fcmTopicEntities.add(fcmTopicEntity);
                    });
                    fcmTopicRepository.saveAll(fcmTopicEntities);
                }
                userInfoRepo.save(userEntity);
            }


            log.info("Success To Registry Token");
        } catch (YOPLEServiceException e) {
            throw e;
        }
    }


    public void deleteFcmToken()  {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            UserEntity userEntity = userInfoRepo.findBySuid(userInfoDto.getSuid());

            List<FcmTopicEntity> fcmTopicEntities;
            if(userEntity.getFcmToken() != null) {
                userEntity.setFcmToken(FCMConstant.EXPIRED);
                fcmTopicEntities = fcmTopicRepository.findAllByFcmToken(userEntity.getFcmToken());

                fcmTopicEntities.forEach(data -> {
                    try {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(Collections.singletonList(data.getFcmToken()), String.valueOf(data.getWorldId()));
                    } catch (FirebaseMessagingException e) {
                        throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
                    }
                });
                fcmTopicRepository.deleteAll(fcmTopicEntities);
                userInfoRepo.save(userEntity);
            }


            log.info("Deleted Fcm Token");

        } catch (YOPLEServiceException e) {
            throw e;
        }
    }

    @Async
    public void sendNotificationToken(String title, String body) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();



            Notification notification = Notification.builder().setTitle(title).setBody(body).build();

            String token = userInfoRepo.findBySuid(userInfoDto.getSuid()).getFcmToken();


            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .build();
            String response = FirebaseMessaging.getInstance(FirebaseApp.getInstance(FCMConstant.FCM_INSTANCE)).send(message);
            log.info("Successfully sent : {}", response);
        } catch (FirebaseMessagingException e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
    }

    @Async
    public void sendNotificationTopic(String title, String body, String topic) {
        try {
            Notification notification = Notification.builder().setTitle(title).setBody(body).build();

            Message message = Message.builder()
                    .setTopic(topic)
                    .setNotification(notification)
                    .build();
            String response = FirebaseMessaging.getInstance(FirebaseApp.getInstance(FCMConstant.FCM_INSTANCE)).send(message);
            log.info("Successfully sent : {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Error : {}", e.getMessagingErrorCode());
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
        }
    }

    public void subscribeToTopic(String token, String topic) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance(FirebaseApp.getInstance(FCMConstant.FCM_INSTANCE)).subscribeToTopic(Collections.singletonList(token), topic);
            log.info("Success To Subscribe : {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Error : {}", e.getMessagingErrorCode());
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
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
