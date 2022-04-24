package com.map.mutual.side.auth.svc.impl;

import com.map.mutual.side.auth.component.SmsSender;
import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.dto.WorldInviteAccept;
import com.map.mutual.side.auth.model.dto.block.UserBlockDto;
import com.map.mutual.side.auth.model.dto.block.UserBlockedDto;
import com.map.mutual.side.auth.model.dto.notification.InvitedNotiDto;
import com.map.mutual.side.auth.model.dto.notification.NotiDto;
import com.map.mutual.side.auth.model.dto.notification.WorldEntryNotiDto;
import com.map.mutual.side.auth.model.dto.report.ReviewReportDto;
import com.map.mutual.side.auth.model.dto.report.UserReportDto;
import com.map.mutual.side.auth.model.entity.*;
import com.map.mutual.side.auth.repository.*;
import com.map.mutual.side.auth.svc.UserService;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.fcmmsg.constant.FCMConstant;
import com.map.mutual.side.common.fcmmsg.svc.FCMService;
import com.map.mutual.side.common.utils.YOPLEUtils;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

    @Autowired
    public UserServiceImpl(WorldUserMappingRepo worldUserMappingRepo, UserInfoRepo userInfoRepo
            , ModelMapper modelMapper, WorldRepo worldRepo, JWTRepo jwtRepo
            , UserWorldInvitingLogRepo userWorldInvitingLogRepo , UserTOSRepo userTOSRepo
            , FCMService fcmService , SmsSender smsSender ,WorldJoinLogRepo worldJoinLogRepo
            , UserBlockLogRepo userBlockLogRepo, UserReportLogRepo userReportLogRepo
            , ReviewReportLogRepo reviewReportLogRepo) {
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
    }

    /**
     * Description : 회원가입.
     * Name        : signUp
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    @Transactional
    public UserInfoDto signUp(UserInfoDto user) {
        UserEntity userEntity = UserEntity.builder()
                .suid(user.getSuid())
                .userId(user.getUserId())
                .name(user.getName())
                .phone(user.getPhone())
                .profileUrl(user.getProfileUrl()).build();

        UserTOSEntity userTOSEntity = UserTOSEntity.builder()
                .suid(user.getSuid())
                .serviceTosYN(user.getUserTOSDto().getServiceTosYN())
                .ageCollectionYn(user.getUserTOSDto().getAgeCollectionYN())
                .locationInfoYn(user.getUserTOSDto().getLocationInfoYN())
                .marketingYn(user.getUserTOSDto().getMarketingYN())
                .userInfoYn(user.getUserTOSDto().getUserInfoYN())
                .build();


        userInfoRepo.save(userEntity);
        userTOSRepo.save(userTOSEntity);
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
    public UserInfoDto findUser(String id, String phone) throws YOPLEServiceException {
        UserEntity userEntity;
        UserInfoDto userInfoDto;

        //폰으로 검색.
        if(StringUtil.isNullOrEmpty(id) == true) {
            userEntity = userInfoRepo.findOneByPhone(phone);
        }
        else { // 유저 ID로 검색.
            userEntity = userInfoRepo.findByUserId(id);
        }


        if(userEntity == null)
            throw new YOPLEServiceException(ApiStatusCode.USER_NOT_FOUND);

        //폰으로 검색.
        if(StringUtil.isNullOrEmpty(id) == true) {
            userInfoDto = UserInfoDto.builder()
                    .suid(userEntity.getSuid())
                    .build();

        }
        else { // 유저 ID로 검색.
            userInfoDto = UserInfoDto.builder()
                    .suid(userEntity.getSuid())
                    .userId(userEntity.getUserId())
                    .name(userEntity.getName())
                    .profileUrl(userEntity.getProfileUrl())
                    .build();
        }


        return userInfoDto;
    }

    /**
     * Description : 월드에 참여하기.
     *  - 월드 초대 코드 유효하지 않으면 WORLD_USER_CDOE_VALID_FAILED
     *  - 사용자 이미 월드에 가입되어있으면 ALREADY_WORLD_MEMEBER
     * Name        : inviteJoinWorld
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public WorldDto JoinWorld( String worldInvitationCode) throws YOPLEServiceException {

        // 1. 사용자 SUID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        // 2. 사용자가 월드에 이미 가입 되어있는지 확인.
        // 월드 초대 코드 유효하지 않으면 WORLD_USER_CDOE_VALID_FAILED Exception Throw
        Long inviteWorldId = worldUserMappingRepo.exsistUserCodeInWorld(worldInvitationCode, userInfoDto.getSuid());

        if (inviteWorldId == null) {
            logger.error("해당 사용자가 이미 월드에 속해있습니다.");
            throw new YOPLEServiceException(ApiStatusCode.ALREADY_WORLD_MEMEBER);
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
                .orElseThrow(() -> new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR));

        // 5. 월드에 참여된 사용자들에게 알림 전송
        CompletableFuture<FCMConstant.ResultType> response = fcmService.sendNotificationTopic(FCMConstant.MSGType.B, world.getWorldId(), userInfoDto.getSuid());
        response.thenAccept(d -> {
            if (d.getType().equals(FCMConstant.ResultType.SUCCESS.getType())) {
                logger.info(d.getDesc());
            } else {
                logger.error(d.getDesc());
            }
        });

        // 6. 참여한 월드 정보 리턴.
        return WorldDto.builder().worldId(world.getWorldId())
                .worldName(world.getWorldName())
                .worldDesc(world.getWorldDesc()).build();

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
                        .orElseThrow( ()-> new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR) );

        // 2. 응답 객체 설정
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .profileUrl(userEntity.getProfileUrl())
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
    public Long getRecentAccessWorldID(String suid)
    {

        // 1. 가장 최근에 접속한 월드 ID 조회
        // 최근에 접속한 월드 ID가 존재하지 않다면 Default 0 리턴.
        WorldUserMappingEntity worldUserMappingEntity = worldUserMappingRepo.findTop1ByUserSuidOrderByAccessTimeDesc(suid)
                .orElse( WorldUserMappingEntity.builder().worldId(0l).build() );


        return worldUserMappingEntity.getWorldId();

    }

    /**
     * Description : 사용자 정보 수정.
     * Name        : userInfoUpdate
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public UserInfoDto userInfoUpdate(String suid, String userId, String profileUrl) {

        // 1. 사용자 SUID 가져오기.
        UserEntity userEntity = userInfoRepo.findById(suid)
                .orElseThrow(()->new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR));

        // 2. 수정 요청 들어온 필드 설정.
        if(StringUtil.isNullOrEmpty(userId) == false)
            userEntity.setUserId(userId);

        if(StringUtil.isNullOrEmpty(profileUrl) == false)
            userEntity.setProfileUrl(profileUrl);

        // 3. 사용자 프로필 정보 수정.
        userInfoRepo.save(userEntity);

        // 4. 업데이트된 사용자 정보 설정
        UserInfoDto userInfoDto  = UserInfoDto.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .profileUrl(userEntity.getProfileUrl())
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
    public void userLogout(String suid) {

        JWTRefreshTokenLogEntity jwtRefreshTokenLogEntity = JWTRefreshTokenLogEntity.builder().userSuid(suid).build();

        jwtRepo.delete(jwtRefreshTokenLogEntity);


    }

    /**
     * Description :  사용자 월드 초대하기.
     *                  - 초대자가 월드에 참여중이 아닌경우 FORBIDDEN Exception
     *                  - 초대받는자가 월드에 참여인경우 ALREADY_WORLD_MEMEBER Exception
     *                  - 초대 수락 대기 중인 경우 ALREADY_WORLD_INVITING_STATUS
     * Name        : userWorldInviting
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public void userWorldInviting(String suid, String targetSuid, Long worldId) throws YOPLEServiceException {

        // 1. 초대자가 월드에 참여중이 아닌경우 FORBIDDEN Exception
        WorldUserMappingEntity suidWorldMapping = worldUserMappingRepo.findOneByWorldIdAndUserSuid(worldId,suid)
                .orElseThrow(()-> new YOPLEServiceException(ApiStatusCode.FORBIDDEN));

        // 2. 사용자가 이미 초대를 받은 경우.
        if(userWorldInvitingLogRepo.findOneByUserSuidAndTargetSuidAndWorldIdAndInvitingStatus(suid,targetSuid,worldId,"-").isPresent())
            throw new YOPLEServiceException(ApiStatusCode.ALREADY_WORLD_INVITING_STATUS);

        // TODO: 2022-04-15  PUSH 알림 보내기 개발 되어야함.
        // 3. 초대받는자가 월드에 참여인경우 ALREADY_WORLD_MEMEBER Exception
        if( worldUserMappingRepo.findOneByWorldIdAndUserSuid(worldId,targetSuid).isPresent() == true )
            throw new YOPLEServiceException(ApiStatusCode.ALREADY_WORLD_MEMEBER);

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
        Optional<WorldUserMappingEntity> worldMapping = worldUserMappingRepo.findOneByWorldIdAndUserSuid(worldId,suid);

        //월드에 가입되어있지 않은 유저인 경우 권한 없음.
        worldMapping.orElseThrow(()-> new YOPLEServiceException(ApiStatusCode.FORBIDDEN));

        String userID = worldMapping.get().getUserEntity().getUserId();
        String phone = worldMapping.get().getUserEntity().getPhone();
        String worldUserCode = worldMapping.get().getWorldUserCode();

        try {
            smsSender.inviteSendMessage(targetPhone, phone, worldUserCode);
        }catch(IOException e){
            throw new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR,"SMS 서비스가 원활하지 않습니다.");
        }

    }

    /**
     * Description : 월드 참여자 조회하기.
     * Name        : worldUsers
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public List<UserInWorld> worldUsers(long worldId, String suid) {
        List<UserInWorld> userInfoEntities;

        userInfoEntities = worldUserMappingRepo.findAllUsersInWorld(worldId, suid);

        return userInfoEntities;
    }

    /**
     * Description : 알림 메시지 리스트 조회
     * Name        : notificationList
     * Author      : 조 준 희
     * History     : [2022-04-13] - 조 준 희 - Create
     */
    @Override
    public NotiDto notificationList(String suid) {


        List<InvitedNotiDto> invitedNotiList =   userWorldInvitingLogRepo.InvitedNotiList(suid);
        List<WorldEntryNotiDto> worldEntryNotiList =  worldUserMappingRepo.WorldEntryNotiList(suid);


        NotiDto notis = NotiDto.builder().topNoti(invitedNotiList.stream().collect(Collectors.toList()))
                        .middleNoti(worldEntryNotiList.stream().collect(Collectors.toList())).
                build();

        return notis;
    }

    /**
     * Description : 월드 초대에 응답하기.
     * isAccept 여부에 따라 수락하기, 거절하기.
     * 수락하기 인 경우 입장 월드 정보 리턴.
     * 거절하기 인 경우 월드 ID 0 리턴.
     *      *  - 월드 초대 코드 유효하지 않으면 WORLD_USER_CDOE_VALID_FAILED
     *      *  - 사용자 이미 월드에 가입되어있으면 ALREADY_WORLD_MEMEBER
     * Name        : inviteJoinWorld
     * Author      : 조 준 희
     * History     : [2022/04/17] - 조 준 희 - Create
     */
    @Override
    @Transactional
    public WorldDto inviteJoinWorld(WorldInviteAccept invited, String suid) {

        // 초대하기인지 수락하기인지 분기

        Optional<UserWorldInvitingLogEntity> inviteLog = userWorldInvitingLogRepo.findById(invited.getInviteNumber());

        //초대장이 존재하지 않는 경우.
        inviteLog.orElseThrow(() -> new YOPLEServiceException(ApiStatusCode.INVITE_NOT_VALID));

        if(inviteLog.get().getTargetSuid().equals(suid) == false                   // 초대대상 SUID 비교.
         || inviteLog.get().getUserSuid().equals(invited.getUserSuid()) == false // 초대자 SUID 비교
                || inviteLog.get().getWorldUserCode().equals(invited.getWorldUserCode()) == false) {  // 월드 초대 코드 비교.
            throw new YOPLEServiceException(ApiStatusCode.INVITE_NOT_VALID);
        }

        //수락
        // 1. 초대장 조회하기, 유효성 체크,
        // 2. 월드 입장 처리
        if(invited.getIsAccept().equals("Y")){
            inviteLog.get().inviteAccept();
            userWorldInvitingLogRepo.save(inviteLog.get());

            //월드에 참여하기 서비스 사용
            return JoinWorld(invited.getWorldUserCode());

        }else {
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
    public void block(String suid, UserBlockDto userBlockDto) {

        // 이미 차단된 유저인지 조회.
        if( userBlockLogRepo.existsByUserSuidAndBlockSuidAndAndIsBlocking(suid, userBlockDto.getBlockSuid(), "Y"))
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
    public void blockCancel(String suid, Long blockId) {

        UserBlockLogEntity log = userBlockLogRepo.findById(blockId)
                                                .orElseThrow(()-> new YOPLEServiceException(ApiStatusCode.FORBIDDEN));

        // 사용자 차단 이력이 아닌 경우
        if(log.getUserSuid().equals(suid) == false)
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
}
