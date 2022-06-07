package com.map.mutual.side.auth.svc.impl;

import com.map.mutual.side.auth.component.SmsSender;
import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.dto.WorldInviteAccept;
import com.map.mutual.side.auth.model.dto.block.UserBlockDto;
import com.map.mutual.side.auth.model.dto.block.UserBlockedDto;
import com.map.mutual.side.auth.model.dto.notification.EmojiNotiDto;
import com.map.mutual.side.auth.model.dto.notification.InvitedNotiDto;
import com.map.mutual.side.auth.model.dto.notification.NotiDto;
import com.map.mutual.side.auth.model.dto.notification.WorldEntryNotiDto;
import com.map.mutual.side.auth.model.dto.notification.extend.notificationDto;
import com.map.mutual.side.auth.model.dto.report.ReviewReportDto;
import com.map.mutual.side.auth.model.dto.report.UserReportDto;
import com.map.mutual.side.auth.model.entity.*;
import com.map.mutual.side.auth.repository.*;
import com.map.mutual.side.auth.svc.UserService;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.exception.YOPLETransactionException;
import com.map.mutual.side.common.fcmmsg.constant.FCMConstant;
import com.map.mutual.side.common.fcmmsg.model.entity.FcmTopicEntity;
import com.map.mutual.side.common.fcmmsg.repository.FcmTopicRepository;
import com.map.mutual.side.common.fcmmsg.svc.FCMService;
import com.map.mutual.side.common.utils.CryptUtils;
import com.map.mutual.side.common.utils.YOPLEUtils;
import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.map.mutual.side.review.repository.EmojiStatusRepo;
import com.map.mutual.side.review.repository.ReviewRepo;
import com.map.mutual.side.review.repository.ReviewWorldMappingRepository;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.model.entity.WorldEntity;
import com.map.mutual.side.world.model.entity.WorldJoinLogEntity;
import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.repository.WorldJoinLogRepo;
import com.map.mutual.side.world.repository.WorldRepo;
import com.map.mutual.side.world.repository.WorldUserMappingRepo;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * fileName       : UserServiceImpl
 * author         : kimjaejung
 * createDate     : 2022/03/16
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/16        kimjaejung       최초 생성
 */
@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private WorldUserMappingRepo worldUserMappingRepo;
    private UserInfoRepo userInfoRepo;
    private ModelMapper modelMapper;
    private WorldRepo worldRepo;
    private WorldJoinLogRepo worldJoinLogRepo;
    private JWTRepo jwtRepo;
    private UserWorldInvitingLogRepo userWorldInvitingLogRepo;
    private UserTOSRepo userTOSRepo;
    private SmsSender smsSender;
    private FCMService fcmService;
    private UserBlockLogRepo userBlockLogRepo;
    private UserReportLogRepo userReportLogRepo;
    private ReviewReportLogRepo reviewReportLogRepo;
    private EmojiStatusRepo emojiStatusRepo;
    private ReviewRepo reviewRepo;
    private ReviewWorldMappingRepository reviewWorldMappingRepository;
    private FcmTopicRepository fcmTopicRepository;

    @Autowired
    public UserServiceImpl(WorldUserMappingRepo worldUserMappingRepo, UserInfoRepo userInfoRepo
            , ModelMapper modelMapper, WorldRepo worldRepo, JWTRepo jwtRepo
            , UserWorldInvitingLogRepo userWorldInvitingLogRepo, UserTOSRepo userTOSRepo
            , FCMService fcmService, SmsSender smsSender, WorldJoinLogRepo worldJoinLogRepo
            , UserBlockLogRepo userBlockLogRepo, UserReportLogRepo userReportLogRepo
            , ReviewReportLogRepo reviewReportLogRepo, EmojiStatusRepo emojiStatusRepo, ReviewRepo reviewRepo
    , ReviewWorldMappingRepository reviewWorldMappingRepository, FcmTopicRepository fcmTopicRepository) {
        this.worldUserMappingRepo = worldUserMappingRepo;
        this.userInfoRepo = userInfoRepo;
        this.modelMapper = modelMapper;
        this.worldRepo = worldRepo;
        this.jwtRepo = jwtRepo;
        this.userWorldInvitingLogRepo = userWorldInvitingLogRepo;
        this.userTOSRepo = userTOSRepo;
        this.fcmService = fcmService;
        this.smsSender = smsSender;
        this.worldJoinLogRepo = worldJoinLogRepo;
        this.userBlockLogRepo = userBlockLogRepo;
        this.userReportLogRepo = userReportLogRepo;
        this.reviewReportLogRepo = reviewReportLogRepo;
        this.emojiStatusRepo = emojiStatusRepo;
        this.reviewRepo = reviewRepo;
        this.reviewWorldMappingRepository = reviewWorldMappingRepository;
        this.fcmTopicRepository = fcmTopicRepository;
    }

    /**
     * Description : 회원가입.
     * Name        : signUp
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    @Transactional
    public UserInfoDto signUp(UserInfoDto user) throws YOPLEServiceException, RuntimeException{
        UserEntity userEntity = UserEntity.builder()
                .suid(user.getSuid())
                .userId(user.getUserId())
                .name(user.getName())
                .phone(user.getPhone())
                .profileUrl(user.getProfileUrl())
                .profilePinUrl(user.getProfilePinUrl()).notiCheckDt(LocalDateTime.now()).build();

        UserTOSEntity userTOSEntity = UserTOSEntity.builder()
                .suid(user.getSuid())
                .serviceTosYN(user.getUserTOSDto().getServiceTosYN())
                .ageCollectionYn(user.getUserTOSDto().getAgeCollectionYN())
                .locationInfoYn(user.getUserTOSDto().getLocationInfoYN())
                .marketingYn(user.getUserTOSDto().getMarketingYN())
                .userInfoYn(user.getUserTOSDto().getUserInfoYN())
                .build();

        if (userInfoRepo.findOneByPhone(user.getPhone()) != null) {
            logger.debug("회원가입 : 이미 가입된 사용자");
            throw new YOPLEServiceException(ApiStatusCode.ALREADY_YOPLE_USER);
        }

        userInfoRepo.save(userEntity);
        userTOSRepo.save(userTOSEntity);

        logger.debug("회원가입 : 사용자 회원가입 완료 + TOS 정보 저장 완료");

        return user;
    }

    /**
     * Description : 사용자 검색(찾기).
     * Name        : findUser
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public UserInfoDto findUser(String id, String phone, String suid) throws YOPLEServiceException {
        UserEntity userEntity;
        UserInfoDto userInfoDto;

        //폰으로 검색.
        if (StringUtil.isNullOrEmpty(id) == true) {
            userEntity = userInfoRepo.findOneByPhone(phone);
        } else { // 유저 ID로 검색.
            userEntity = userInfoRepo.findByUserId(id);
        }


        if (userEntity == null)
            throw new YOPLEServiceException(ApiStatusCode.USER_NOT_FOUND);

        // 차단된 사용자 리스트 조회
        List<UserBlockLogEntity> blocks = userBlockLogRepo.findByUserSuidAndIsBlocking(suid, "Y");

        // 사용자 검색된 유저가 차단된 사용자라면 사용자 없음.
        for (UserBlockLogEntity blockUser : blocks) {
            if (blockUser.getBlockSuid().equals(userEntity.getSuid()))
                throw new YOPLEServiceException(ApiStatusCode.USER_NOT_FOUND);
        }


        //폰으로 검색.
        if (StringUtil.isNullOrEmpty(id) == true) {
            userInfoDto = UserInfoDto.builder()
                    .suid(CryptUtils.AES_Encode(userEntity.getSuid()))
                    .build();

        } else { // 유저 ID로 검색.
            userInfoDto = UserInfoDto.builder()
                    .suid(CryptUtils.AES_Encode(userEntity.getSuid()))
                    .userId(userEntity.getUserId())
                    .name(userEntity.getName())
                    .profileUrl(userEntity.getProfileUrl())
                    .build();
        }


        return userInfoDto;
    }

    /**
     * Description : 월드에 참여하기.
     * - 월드 초대 코드 유효하지 않으면 WORLD_USER_CDOE_VALID_FAILED
     * - 사용자 이미 월드에 가입되어있으면 ALREADY_WORLD_MEMEBER
     * Name        : inviteJoinWorld
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public WorldDto JoinWorld(String worldInvitationCode) throws YOPLEServiceException, ExecutionException, InterruptedException {

        // 1. 사용자 SUID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        // 2. 사용자가 월드에 이미 가입 되어있는지 확인.
        // 월드 초대 코드 유효하지 않으면 WORLD_USER_CDOE_VALID_FAILED Exception Throw
        Long inviteWorldId = worldUserMappingRepo.exsistUserCodeInWorld(worldInvitationCode, userInfoDto.getSuid());


        if (inviteWorldId == null) {
            logger.error("사용자가 이미 월드에 속해있습니다.");
            throw new YOPLEServiceException(ApiStatusCode.ALREADY_WORLD_MEMEBER);
        }

        // 타인의 월드는 최대 20개 까지만 입장 가능.
        Long worldCnt = worldUserMappingRepo.countAllByActiveWorlds(userInfoDto.getSuid());
        if (worldCnt >= 20){
            logger.debug("월드에 참여하기 : 월드 최대 입장 수 초과.");
            throw new YOPLEServiceException(ApiStatusCode.EXCEEDED_LIMITED_COUNT);
        }

        // 3. 초대 수락한 월드 입장 처리
        WorldUserMappingEntity worldUserMappingEntity = WorldUserMappingEntity.builder()
                .userSuid(userInfoDto.getSuid())
                .worldId(inviteWorldId)
                .worldUserCode(YOPLEUtils.getWorldRandomCode())
                .worldinvitationCode(worldInvitationCode)
                .accessTime(LocalDateTime.now())
                .build();

        WorldJoinLogEntity worldJoinLog = WorldJoinLogEntity.builder().worldId(inviteWorldId)
                .userSuid(userInfoDto.getSuid())
                .build();

        worldUserMappingRepo.save(worldUserMappingEntity);
        worldJoinLogRepo.save(worldJoinLog);

        // 4. 참여한 월드 정보 조회
        WorldEntity world = worldRepo.findById(worldUserMappingEntity.getWorldId())
                .orElseThrow(() -> {
                    logger.error("월드에 참여하기 : 참여한 월드({})의 정보가 존재하지 않습니다. ",worldUserMappingEntity.getWorldId());
                    return new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR, "참여한 월드의 정보가 존재하지 않습니다.");
                });

        // 5. 월드에 참여된 사용자들에게 알림 전송 ---동기처리---
        logger.debug("월드에 참여하기 : 월드에 참여된 사용자들 FCM 알림 전송 Start ");
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return fcmService.sendNotificationTopic(FCMConstant.MSGType.B, world.getWorldId(), userInfoDto.getSuid());
            } catch (YOPLEServiceException e) {
                return false;
            }
        });
        logger.debug("월드에 참여하기 : 월드에 참여된 사용자들 FCM 알림 전송 End ");

        // 6. 월드에 참여.
        if (completableFuture.get()) {
            String fcmToken = userInfoRepo.findBySuid(userInfoDto.getSuid()).getFcmToken();
            fcmTopicRepository.save(FcmTopicEntity.builder().worldId(inviteWorldId).fcmToken(fcmToken).build());

            return WorldDto.builder().worldId(world.getWorldId())
                    .worldName(world.getWorldName())
                    .worldDesc(world.getWorldDesc()).build();
        } else {
            logger.error("월드에 참여하기 : FCM 월드 사용자에게 알림전송 실패");
            throw new YOPLETransactionException(ApiStatusCode.FAIL_JOIN_WORLD);
        }
        // 7. 참여한 월드 정보 리턴.

    }

    /**
     * Description : 유저 상세정보 조회하기.
     * Name        : userDetails
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public UserInfoDto userDetails(String suid) throws YOPLEServiceException {

        //1. 토큰에 저장된 SUID 사용자가 없을 경우. Exception.
        UserEntity userEntity = userInfoRepo.findById(suid)
                .orElseThrow(() -> {
                    logger.error("사용자 상세정보 조회 : 존재하지 않는 SUID로 상세정보 조회.");
                    return new YOPLEServiceException(ApiStatusCode.UNAUTHORIZED);});

        // 2. 응답 객체 설정
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .profileUrl(userEntity.getProfileUrl())
                .notiCheckDt(userEntity.getNotiCheckDt())
                .build();

        // 3. 리턴.
        return userInfoDto;

    }

    /**
     * Description : 가장 최근 입장한 월드 ID 조회
     * Name        : getRecentAccessWorldID
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    public Long getRecentAccessWorldID(String suid) {

        // 1. 가장 최근에 접속한 월드 ID 조회
        // 최근에 접속한 월드 ID가 존재하지 않다면 Default 0 리턴.
        WorldUserMappingEntity worldUserMappingEntity = worldUserMappingRepo.findTop1ByUserSuidOrderByAccessTimeDesc(suid)
                .orElse(WorldUserMappingEntity.builder().worldId(0l).build());


        return worldUserMappingEntity.getWorldId();

    }

    /**
     * Description : 사용자 프로필 수정.
     * - 사용자 ID 이미 사용중이면, USER_ID_OVERLAPS 예외.
     * Name        : userInfoUpdate
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public UserInfoDto userInfoUpdate(String suid, String userId, String profileUrl, String profilePinUrl) throws YOPLEServiceException {

        // 1. 사용자 SUID 가져오기.
        UserEntity userEntity = userInfoRepo.findById(suid)
                .orElseThrow(() -> {
                    logger.error("사용자 프로필 수정 : SUID ({})의 사용자 존재하지 않음.",suid );
                    return new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR, String.format(" SUID (%s)의 사용자 존재하지 않음.",suid));
                });

        // 2. 수정 요청 들어온 필드 설정.
        if (StringUtil.isNullOrEmpty(userId) == false)
            userEntity.setUserId(userId);

        if (StringUtil.isNullOrEmpty(profileUrl) == false) {
            userEntity.setProfileUrl(profileUrl);
            userEntity.setProfilePinUrl(profilePinUrl);
        }

        UserEntity idUser = userInfoRepo.findByUserId(userId);

        // id 사용자가 있고, 해당 사용자가 아닌 경우.
        if (idUser != null && idUser.getSuid().equals(suid) == false) {
            logger.error("사용자 프로필 수정 : ID 이미 사용중입니다. 프론트 ID 중복 방식 체크 필요.!!");
            throw new YOPLEServiceException(ApiStatusCode.USER_ID_OVERLAPS);
        }

        // 3. 사용자 프로필 정보 수정.
        userInfoRepo.save(userEntity);

        // 4. 업데이트된 사용자 정보 설정
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .profileUrl(userEntity.getProfileUrl())
                .profilePinUrl(userEntity.getProfilePinUrl())
                .build();

        // 5. 리턴.
        return userInfoDto;

    }

    /**
     * Description : 로그아웃
     * Name        : userLogout
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public void userLogout(String suid) throws RuntimeException {

        JWTRefreshTokenLogEntity jwtRefreshTokenLogEntity = JWTRefreshTokenLogEntity.builder().userSuid(suid).build();

        // 이미 저장된 객체
        jwtRefreshTokenLogEntity.isPersist();

        jwtRepo.delete(jwtRefreshTokenLogEntity);

    }

    /**
     * Description :  사용자 월드 초대하기.
     * - 초대자가 월드에 참여중이 아닌경우 FORBIDDEN Exception
     * - 초대받는자가 월드에 참여인경우 ALREADY_WORLD_MEMEBER Exception
     * - 초대 수락 대기 중인 경우 ALREADY_WORLD_INVITING_STATUS
     * Name        : userWorldInviting
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public void userWorldInviting(String suid, String targetSuid, Long worldId) throws YOPLEServiceException {

        // 1. 초대자가 월드에 참여중이 아닌경우 FORBIDDEN Exception
        WorldUserMappingEntity suidWorldMapping = worldUserMappingRepo.findOneByWorldIdAndUserSuid(worldId, suid)
                .orElseThrow(() -> new YOPLEServiceException(ApiStatusCode.FORBIDDEN));

        // 2. 사용자가 이미 초대를 받은 경우.
        if (userWorldInvitingLogRepo.findOneByUserSuidAndTargetSuidAndWorldIdAndInvitingStatus(suid, targetSuid, worldId, "-").isPresent())
            throw new YOPLEServiceException(ApiStatusCode.ALREADY_WORLD_INVITING_STATUS);

        // 3. 초대받는자가 월드에 참여인경우 ALREADY_WORLD_MEMEBER Exception
        if (worldUserMappingRepo.findOneByWorldIdAndUserSuid(worldId, targetSuid).isPresent() == true)
            throw new YOPLEServiceException(ApiStatusCode.ALREADY_WORLD_MEMEBER);

        // 4. 차단 당했는지 여부. 차단 당했다면 바로 OK(200) 리턴
        // 차단 여부 보다. 1,2,3의 예외가 우선이기에 차단여부가이 가장 마지막.
        if (userBlockLogRepo.existsByUserSuidAndBlockSuidAndIsBlocking(targetSuid, suid, "Y")) {
            throw new YOPLEServiceException(ApiStatusCode.OK);
        }

        // 월드 참여 매핑 설정
        UserWorldInvitingLogEntity userWorldInvitingLogEntity = UserWorldInvitingLogEntity.builder()
                .targetSuid(targetSuid)
                .userSuid(suid)
                .worldId(worldId)
                .invitingStatus("-")
                .worldUserCode(suidWorldMapping.getWorldUserCode())
                .build();

        // 월드에 참여.
        userWorldInvitingLogRepo.save(userWorldInvitingLogEntity);

    }

    /**
     * Description : 미 가입 사용자 YOPLE 월드 초대하기 문자.
     * - 월드에 가입되어있지 않은 유저가 월드 초대 시 권한 없음.
     * Name        : unSignedUserWorldInviting
     * Author      : 조 준 희
     * History     : [2022/04/17] - 조 준 희 - Create
     */
    @Override
    public void unSignedUserWorldInviting(String suid, String targetPhone, Long worldId) throws YOPLEServiceException {

        /// 초대자 정보 가져오기.
        Optional<WorldUserMappingEntity> worldMapping = worldUserMappingRepo.findOneByWorldIdAndUserSuid(worldId, suid);

        //월드에 가입되어있지 않은 유저인 경우 권한 없음.
        worldMapping.orElseThrow(() -> new YOPLEServiceException(ApiStatusCode.FORBIDDEN));

        String phone = worldMapping.get().getUserEntity().getPhone();
        String worldUserCode = worldMapping.get().getWorldUserCode();

        try {
            smsSender.inviteSendMessage(targetPhone, phone, worldUserCode);
        } catch (IOException e) {
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR, "SMS 서비스가 원활하지 않습니다.");
        }

    }

    /**
     * Description : 월드 참여자 조회하기.
     * Name        : worldUsers
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public List<UserInWorld> worldUsers(long worldId, String suid) throws Exception {
        List<UserInWorld> userInfoEntities;

        userInfoEntities = worldUserMappingRepo.findAllUsersInWorld(worldId, suid);
        List<UserBlockLogEntity> blocks = userBlockLogRepo.findByUserSuidAndIsBlocking(suid, "Y");

        // suid 암호화.
        // 차단된 사용자 제거. 필터링.
        for (int i = 0; i < userInfoEntities.size(); i++) {
            UserInWorld user = userInfoEntities.get(i);
            if (blocks.stream().anyMatch(v -> v.getBlockSuid().equals(user.getSuid()))) {
                userInfoEntities.remove(i);
                i--;
                continue;
            }

            user.suidChange(CryptUtils.AES_Encode(user.getSuid()));
        }
        return userInfoEntities;
    }

    /**
     * Description : 알림 메시지 리스트 조회
     * Name        : notificationList
     * Author      : 조 준 희
     * History     : [2022-04-13] - 조 준 희 - Create
     */
    @Override
    public NotiDto notificationList(String suid) throws Exception {


        List<InvitedNotiDto> invitedNotiList = userWorldInvitingLogRepo.InvitedNotiList(suid);

        for (InvitedNotiDto noti : invitedNotiList)
            noti.decodingSuid();



        List<WorldEntryNotiDto> worldEntryNotiList = worldUserMappingRepo.WorldEntryNotiList(suid).stream().sorted(
                Comparator.comparing(v -> Timestamp.valueOf(((WorldEntryNotiDto) v).PushDate()).getTime()).reversed()
        ).collect(Collectors.toList());

        List<EmojiNotiDto> emojiNotiDtos = emojiStatusRepo.findEmojiNotis(suid).stream().sorted(
                Comparator.comparing(v -> Timestamp.valueOf(((EmojiNotiDto) v).PushDate()).getTime()).reversed()
        ).collect(Collectors.toList());

        logger.debug("알림 조회하기 : 월드 초대알림({}개), 월드 입장알림({}개), 리뷰 소식알림({}개)",invitedNotiList.stream().count(), worldEntryNotiList.stream().count(),emojiNotiDtos.stream().count());
        List<notificationDto> middleNoti = new ArrayList<>();


        while ((emojiNotiDtos.isEmpty() || worldEntryNotiList.isEmpty()) == false) {
            if (emojiNotiDtos.isEmpty()) {
                middleNoti.addAll(worldEntryNotiList);
                break;
            }
            if (worldEntryNotiList.isEmpty()) {
                middleNoti.addAll(emojiNotiDtos);
                break;
            }

            if (emojiNotiDtos.get(0).PushDate().isAfter(worldEntryNotiList.get(0).PushDate()))
                middleNoti.add(emojiNotiDtos.remove(0));
            else
                middleNoti.add(worldEntryNotiList.remove(0));
        }

        if (emojiNotiDtos.isEmpty() == false) {
            middleNoti.addAll(emojiNotiDtos);
        }
        if (worldEntryNotiList.isEmpty() == false) {
            middleNoti.addAll(worldEntryNotiList);
        }
        logger.debug("알림 조회하기 : Top 알림({}개), Middle 알림({}개)",invitedNotiList.stream().count(), middleNoti.stream().count());

        NotiDto notis = NotiDto.builder().topNoti(invitedNotiList.stream().collect(Collectors.toList()))
                .middleNoti(middleNoti).
                build();

        return notis;
    }

    /**
     * Description : 월드 초대에 응답하기.
     * isAccept 여부에 따라 수락하기, 거절하기.
     * 수락하기 인 경우 입장 월드 정보 리턴.
     * 거절하기 인 경우 월드 ID 0 리턴.
     * *  - 월드 초대 코드 유효하지 않으면 WORLD_USER_CDOE_VALID_FAILED
     * *  - 사용자 이미 월드에 가입되어있으면 ALREADY_WORLD_MEMEBER
     * Name        : inviteJoinWorld
     * Author      : 조 준 희
     * History     : [2022/04/17] - 조 준 희 - Create
     */
    @Override
    @Transactional
    public WorldDto inviteJoinWorld(WorldInviteAccept invited, String suid) throws YOPLEServiceException, ExecutionException, InterruptedException {

        // 초대하기인지 수락하기인지 분기
        Optional<UserWorldInvitingLogEntity> inviteLog = userWorldInvitingLogRepo.findById(invited.getInviteNumber());

        //초대장이 존재하지 않는 경우.
        inviteLog.orElseThrow(() -> new YOPLEServiceException(ApiStatusCode.INVITE_NOT_VALID));

        if (inviteLog.get().getTargetSuid().equals(suid) == false                   // 초대대상 SUID 비교.
                || inviteLog.get().getUserSuid().equals(invited.getUserSuid()) == false // 초대자 SUID 비교
                || inviteLog.get().getWorldUserCode().equals(invited.getWorldUserCode()) == false) {  // 월드 초대 코드 비교.
            logger.error("월드 초대에 응답하기 : 초대장 정보가 유효하지 않습니다. { DB 초대장 ({}), INPUT 초대장 ({}) }",inviteLog.get().toString(), invited.toString());
            throw new YOPLEServiceException(ApiStatusCode.INVITE_NOT_VALID);
        }

        //수락
        // 1. 초대장 조회하기, 유효성 체크,
        // 2. 월드 입장 처리
        if (invited.getIsAccept().equals("Y")) {

            //월드에 참여하기 서비스 사용
            WorldDto world = JoinWorld(invited.getWorldUserCode());
            inviteLog.get().inviteAccept();
            userWorldInvitingLogRepo.save(inviteLog.get());

            return world;
        } else {
            //거절
            // 1. 초대장 거절처리.
            inviteLog.get().inviteReject();
            userWorldInvitingLogRepo.save(inviteLog.get());
            return WorldDto.builder().worldId(0L).build();
        }

    }

    /**
     * Description : 사용자 신고하기.
     * Name        : report
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @Override
    public void report(String suid, UserReportDto userReportDto) {

        UserReportLogEntity report = UserReportLogEntity.builder().userSuid(suid)
                .reportSuid(userReportDto.getReportSuid())
                .reportTitle(userReportDto.getReportTitle())
                .reportDesc(userReportDto.getReportDesc())
                .build();

        userReportLogRepo.save(report);

    }

    /**
     * Description : 사용자 차단하기.
     * - 이미 차단된 유저인경우 ALREADY_USER_BLOCKING
     * Name        : block
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @Override
    public void block(String suid, UserBlockDto userBlockDto) throws YOPLEServiceException {

        // 이미 차단된 유저인지 조회.
        if (userBlockLogRepo.existsByUserSuidAndBlockSuidAndIsBlocking(suid, userBlockDto.getBlockSuid(), "Y"))
            throw new YOPLEServiceException(ApiStatusCode.ALREADY_USER_BLOCKING);

        UserBlockLogEntity block = UserBlockLogEntity.builder()
                .blockSuid(userBlockDto.getBlockSuid())
                .isBlocking("Y")
                .userSuid(suid)
                .build();

        userBlockLogRepo.save(block);

    }

    /**
     * Description : 사용자 차단해지하기.
     * - 없는 차단 이력 요청할 경우 FORBIDDEN
     * - 사용자 차단 이력 아닌 경우 FORBIDDEN
     * Name        : blockCancel
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @Override
    public void blockCancel(String suid, Long blockId) throws YOPLEServiceException {

        UserBlockLogEntity log = userBlockLogRepo.findById(blockId)
                .orElseThrow(() -> new YOPLEServiceException(ApiStatusCode.FORBIDDEN));

        // 사용자 차단 이력이 아닌 경우
        if (log.getUserSuid().equals(suid) == false)
            throw new YOPLEServiceException(ApiStatusCode.FORBIDDEN);

        // isBlocking N으로 변경.
        log.blockCancel();

        userBlockLogRepo.save(log);

    }

    /**
     * Description : 사용자 차단리스트 조회.
     * Name        : block
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @Override
    public List<UserBlockedDto> getBlock(String suid) {

        List<UserBlockedDto> users = userBlockLogRepo.findBlockList(suid);
        return users;
    }

    /**
     * Description : 리뷰 신고하기.
     * Name        : reviewReport
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @Override
    public void reviewReport(String suid, ReviewReportDto reviewReportDto) {

        ReviewReportLogEntity report = ReviewReportLogEntity.builder()
                .reviewId(reviewReportDto.getReviewId())
                .userSuid(suid)
                .reportTitle(reviewReportDto.getReportTitle())
                .reportDesc(reviewReportDto.getReportDesc())
                .build();

        reviewReportLogRepo.save(report);

    }

    @Override
    @Transactional
    public void userWithdrawal() throws YOPLEServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();


        fcmService.deleteFcmToken(userInfoDto.getSuid());
        logger.debug("회원 탈퇴 : FCM 토큰 삭제");
        jwtRepo.deleteByUserSuid(userInfoDto.getSuid());
        logger.debug("회원 탈퇴 : JWT 토큰 삭제");

        List<ReviewEntity> reviews = reviewRepo.findAllByUserEntity(UserEntity.builder().suid(userInfoDto.getSuid()).build());
        logger.debug("회원 탈퇴 : 삭제 대상 리뷰 ({}개)",reviews.stream().count());
        reviews.forEach(review -> {
            reviewWorldMappingRepository.deleteAllByReviewEntity(review);
            logger.debug("회원 탈퇴 : 삭제된 리뷰 id ({})",review.getReviewId());
        });

        reviewRepo.deleteAllByUserEntity(UserEntity.builder().suid(userInfoDto.getSuid()).build());
        logger.debug("회원 탈퇴 : 사용자 리뷰 매핑 삭제");

        worldUserMappingRepo.deleteByUserSuid(userInfoDto.getSuid());
        logger.debug("회원 탈퇴 : 사용자 월드 매핑 삭제");

        userTOSRepo.deleteBySuid(userInfoDto.getSuid());
        logger.debug("회원 탈퇴 : TOS 정보 삭제");

        userInfoRepo.deleteBySuid(userInfoDto.getSuid());
        logger.debug("회원 탈퇴 : 사용자 정보 삭제");
    }

    /**
     * Description : 유저의 독바 알림 갱신 시간 최신화.
     * Name        : notiCheckDtUpdate
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @Override
    public void notiCheckDtUpdate(String suid) throws YOPLEServiceException{

        UserEntity user = userInfoRepo.findById(suid).orElseThrow( () -> {

            YOPLEServiceException e = new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
            e.getResponseJsonObject().getMeta().setMsg("존재하지 않는 사용자입니다.");
            return e;
        });

        // 현재 시간으로 갱신
        user.updateNotiCheckDt();

        // 저장.
        userInfoRepo.save(user);

    }

    /**
     * Description : 유저의 독바 알림 최신 여부 확인
     * Name        : newNotiCheck
     * Author      : 조 준 희
     * History     : [2022-04-21] - 조 준 희 - Create
     */
    @Override
    public boolean newNotiCheck(String suid) throws YOPLEServiceException{

        // 체크 순서는  최신 알림이 있을 것 같은 순서로 진행.

        UserEntity user = userInfoRepo.findById(suid).orElseThrow( () -> {

            YOPLEServiceException e = new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR);
            e.getResponseJsonObject().getMeta().setMsg("존재하지 않는 사용자입니다.");
            return e;
        });

        // 1. 월드 입장 알림 체크
        if( worldUserMappingRepo.existsNewNoti(suid, user.getNotiCheckDt()) == true )
            return true;

        // 2. 이모지 알림 체크
        if( userWorldInvitingLogRepo.existsNewNoti(suid, user.getNotiCheckDt()) == true)
            return true;

        // 3. 월드 초대 알림 체크
        if(emojiStatusRepo.existsNewNoti(suid, user.getNotiCheckDt()) == true)
            return true;

        return false;

    }


}
