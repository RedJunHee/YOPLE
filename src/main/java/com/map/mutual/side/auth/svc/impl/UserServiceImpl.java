package com.map.mutual.side.auth.svc.impl;

import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.JWTRefreshTokenLogEntity;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.model.entity.UserTOSEntity;
import com.map.mutual.side.auth.model.entity.UserWorldInvitingLogEntity;
import com.map.mutual.side.auth.repository.JWTRepo;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.auth.repository.UserTOSRepo;
import com.map.mutual.side.auth.repository.UserWorldInvitingLogRepo;
import com.map.mutual.side.world.repository.WorldUserMappingRepo;
import com.map.mutual.side.auth.svc.UserService;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.utils.YOPLEUtils;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.model.entity.WorldEntity;
import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.repository.WorldRepo;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
@Log4j2
public class UserServiceImpl implements UserService {
    private final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private WorldUserMappingRepo worldUserMappingRepo;
    private UserInfoRepo userInfoRepo;
    private ModelMapper modelMapper;
    private WorldRepo worldRepo;
    private JWTRepo jwtRepo;
    private UserWorldInvitingLogRepo userWorldInvitingLogRepo;
    private UserTOSRepo userTOSRepo;

    @Autowired
    public UserServiceImpl(WorldUserMappingRepo worldUserMappingRepo, UserInfoRepo userInfoRepo
            , ModelMapper modelMapper, WorldRepo worldRepo, JWTRepo jwtRepo
            , UserWorldInvitingLogRepo userWorldInvitingLogRepo
            , UserTOSRepo userTOSRepo) {
        this.worldUserMappingRepo = worldUserMappingRepo;
        this.userInfoRepo = userInfoRepo;
        this.modelMapper = modelMapper;
        this.worldRepo = worldRepo;
        this.jwtRepo = jwtRepo;
        this.userWorldInvitingLogRepo = userWorldInvitingLogRepo;
        this.userTOSRepo = userTOSRepo;
    }

    /**
     * Name        : signUp
     * Author      : 조 준 희
     * Description : 회원가입.
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
                .ageCollectionYn(user.getUserTOSDto().getAgeCollectionYn())
                .locationInfoYn(user.getUserTOSDto().getLocationInfoYn())
                .marketingYn(user.getUserTOSDto().getMarketingYn())
                .userInfoYn(user.getUserTOSDto().getUserInfoYn())
                .build();


        userInfoRepo.save(userEntity);
        userTOSRepo.save(userTOSEntity);
        return user;
    }

    /**
     * Name        : findUser
     * Author      : 조 준 희
     * Description : 사용자 검색(찾기).
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
     * Name        : inviteJoinWorld
     * Author      : 조 준 희
     * Description : 월드에 참여하기.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public WorldDto inviteJoinWorld( String worldInvitationCode) throws YOPLEServiceException {

        // 1. 사용자 SUID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        // 2. 사용자가 월드에 이미 가입 되어있는지 확인.
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

        worldUserMappingRepo.save(worldUserMappingEntity);

        // 4. 참여한 월드 정보 조회
        WorldEntity world = worldRepo.findById(worldUserMappingEntity.getWorldId())
                .orElseThrow(() -> new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR));

        // 5. 참여한 월드 정보 리턴.
        return WorldDto.builder().worldId(world.getWorldId())
                .worldName(world.getWorldName())
                .worldDesc(world.getWorldDesc()).build();

    }

    /**
     * Name        : userDetails
     * Author      : 조 준 희
     * Description : 유저 상세정보 조회하기.
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
     * Name        : getRecentAccessWorldID
     * Author      : 조 준 희
     * Description : 가장 최근 입장한 월드 ID 조회
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
     * Name        : userInfoUpdate
     * Author      : 조 준 희
     * Description : 사용자 정보 수정.
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
     * Name        : userLogout
     * Author      : 조 준 희
     * Description : 로그아웃
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public void userLogout(String suid) {

        JWTRefreshTokenLogEntity jwtRefreshTokenLogEntity = JWTRefreshTokenLogEntity.builder().userSuid(suid).build();

        jwtRepo.delete(jwtRefreshTokenLogEntity);


    }

    /**
     * Name        : userWorldInviting
     * Author      : 조 준 희
     * Description :  사용자 월드 초대하기.
     *                  - 초대자가 월드에 참여중이 아닌경우 FORBIDDEN Exception
     *                  - 초대받는자가 월드에 참여인경우 ALREADY_WORLD_MEMEBER Exception
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public void userWorldInviting(String suid, String targetSuid, Long worldId) throws YOPLEServiceException {

        // 1. 초대자가 월드에 참여중이 아닌경우 FORBIDDEN Exception
        WorldUserMappingEntity suidWorldMapping = worldUserMappingRepo.findByWorldIdAndUserSuid(worldId,suid)
                .orElseThrow(()-> new YOPLEServiceException(ApiStatusCode.FORBIDDEN));

        // 2. 초대받는자가 월드에 참여인경우 ALREADY_WORLD_MEMEBER Exception
        if( worldUserMappingRepo.findOneByWorldIdAndUserSuid(worldId,suid).isPresent() == true )
            throw new YOPLEServiceException(ApiStatusCode.ALREADY_WORLD_MEMEBER);


        // 초대받은 초대 코드.
        String worldinvitationCode = suidWorldMapping.getWorldUserCode();

        // 월드 참여 매핑 설정
        UserWorldInvitingLogEntity userWorldInvitingLogEntity = UserWorldInvitingLogEntity.builder()
                .targetSuid(targetSuid)
                .userSuid(suid)
                .worldinvitationCode(worldinvitationCode)
                .build();

        // 월드에 참여.
        userWorldInvitingLogRepo.save(userWorldInvitingLogEntity);

    }

    /**
     * Name        : worldUsers
     * Author      : 조 준 희
     * Description : 월드 참여자 조회하기.
     * // TODO: 2022/04/10  월드 참여자 조회 권한이 있는지 확인하기.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public List<UserInWorld> worldUsers(long worldId, String suid) {
        List<UserInWorld> userInfoEntities;

        userInfoEntities = worldUserMappingRepo.findAllUsersInWorld(worldId, suid);

        return userInfoEntities;
    }


}
