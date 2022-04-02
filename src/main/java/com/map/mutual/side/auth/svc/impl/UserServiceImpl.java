package com.map.mutual.side.auth.svc.impl;

import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.JWTRefreshTokenLogEntity;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.model.entity.UserWorldInvitingLogEntity;
import com.map.mutual.side.auth.repository.JWTRepo;
import com.map.mutual.side.auth.repository.UserInfoRepo;
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

    @Autowired
    public UserServiceImpl(WorldUserMappingRepo worldUserMappingRepo, UserInfoRepo userInfoRepo
            , ModelMapper modelMapper, WorldRepo worldRepo, JWTRepo jwtRepo
            , UserWorldInvitingLogRepo userWorldInvitingLogRepo) {
        this.worldUserMappingRepo = worldUserMappingRepo;
        this.userInfoRepo = userInfoRepo;
        this.modelMapper = modelMapper;
        this.worldRepo = worldRepo;
        this.jwtRepo = jwtRepo;
        this.userWorldInvitingLogRepo = userWorldInvitingLogRepo;
    }

    @Override
    public UserInfoDto findUser(String id, String phone) {
        UserEntity userEntity;
        UserInfoDto userInfoDto;
        try {

            if(StringUtil.isNullOrEmpty(id) == true)
                userEntity = userInfoRepo.findOneByPhone(phone);
            else
                userEntity = userInfoRepo.findByUserId(id);

            userInfoDto = modelMapper.map(userEntity, UserInfoDto.class);
        } catch (YOPLEServiceException e) {
            log.error("사용자를 찾을 수 없습니다.");
            throw e;
        }
        return userInfoDto;
    }

    //3. 월드 초대 수락하기.
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public WorldDto inviteJoinWorld( String worldinvitationCode) {

        try {

            // 1. 사용자 SUID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            // 2. 사용자가 월드에 이미 가입 되어있는지 확인.
            Long inviteWorldId = worldUserMappingRepo.exsistUserCodeInWorld(worldinvitationCode, userInfoDto.getSuid());

            if (inviteWorldId == null) {
                logger.error("해당 사용자가 이미 월드에 속해있습니다.");
                throw new YOPLEServiceException(ApiStatusCode.ALREADY_WORLD_MEMEBER);
            }

            // 3. 초대 수락한 월드 입장 처리
            WorldUserMappingEntity worldUserMappingEntity = WorldUserMappingEntity.builder()
                    .userSuid(userInfoDto.getSuid())
                    .worldId(inviteWorldId)
                    .worldUserCode(YOPLEUtils.getWorldRandomCode())
                    .worldinvitationCode(worldinvitationCode)
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


        } catch (YOPLEServiceException e) {
            logger.error("World inviteJoinWorld Failed.!! : " + e.getMessage());
            throw e;
        }
    }

    //유저 상세정보 조회하기.
    @Override
    public UserInfoDto userDetails(String suid) {
        try{

            //토큰에 저장된 SUID의 사용자가 없을 경우.
            UserEntity userEntity = userInfoRepo.findById(suid)
                            .orElseThrow( ()->new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR) );

            UserInfoDto userInfoDto = UserInfoDto.builder()
                    .userId(userEntity.getUserId())
                    .name(userEntity.getName())
                    .profileUrl(userEntity.getProfileUrl())
                    .build();

            return userInfoDto;

        }catch(YOPLEServiceException e){
            logger.error("사용자 상세정보 조회 없는 SUID 조회.");
            throw e;
        }catch(Exception e){
            throw e;
        }
    }

    // 사용자 정보 수정
    @Override
    public UserInfoDto userInfoUpdate(String suid, String userId, String profileUrl) {

        try{
            //사용자 정보 가져오기.
            UserEntity userEntity = userInfoRepo.findById(suid)
                    .orElseThrow(()->new YOPLEServiceException(ApiStatusCode.SYSTEM_ERROR));

            if(StringUtil.isNullOrEmpty(userId) == false)
                userEntity.setUserId(userId);

            if(StringUtil.isNullOrEmpty(profileUrl) == false)
                userEntity.setProfileUrl(profileUrl);

            userInfoRepo.save(userEntity);


            UserInfoDto userInfoDto  = UserInfoDto.builder()
                    .userId(userEntity.getUserId())
                    .name(userEntity.getName())
                    .profileUrl(userEntity.getProfileUrl())
                    .build();

            return userInfoDto;

        }catch(YOPLEServiceException e){
            logger.error("사용자 정보 수정 Failed.!! : SUID에 해당하는 사용자를 찾을 수 없음. ");
            throw e;
        }catch(Exception e) {
            throw e;
        }
    }

    //로그아웃
    @Override
    public void userLogout(String suid) {
        try{

            JWTRefreshTokenLogEntity jwtRefreshTokenLogEntity = JWTRefreshTokenLogEntity.builder().userSuid(suid).build();

            jwtRepo.delete(jwtRefreshTokenLogEntity);

        }catch(YOPLEServiceException e){
            throw  e;
        }catch(Exception e){
            throw e;
        }

    }

    //사용자 월드 초대하기.
    @Override
    public void userWorldInviting(String suid, String targetSuid, Long worldId) {

        try{

            // todo 중복 체크 없음. 여러번 초대하기 가능.

            WorldUserMappingEntity worldUserMappingEntity = worldUserMappingRepo.findByWorldIdAndUserSuid(worldId,suid)
                    .orElseThrow(()-> new YOPLEServiceException(ApiStatusCode.FORBIDDEN));

            String worldinvitationCode = worldUserMappingEntity.getWorldUserCode();

            UserWorldInvitingLogEntity userWorldInvitingLogEntity = UserWorldInvitingLogEntity.builder()
                    .targetSuid(targetSuid)
                    .userSuid(suid)
                    .worldinvitationCode(worldinvitationCode)
                    .build();

            userWorldInvitingLogRepo.save(userWorldInvitingLogEntity);

        }catch(YOPLEServiceException e){
            logger.error("월드 사용자 초대하기 실패. : " + e.getMessage());
            throw e;

        }catch(Exception e){
            throw e;
        }

    }

    // 월드 참여자 조회하기.
    @Override
    public List<UserInWorld> worldUsers(long worldId) {
        List<UserInWorld> userInfoEntities;
        try {
            userInfoEntities = worldUserMappingRepo.findAllUsersInWorld(worldId);


             } catch (YOPLEServiceException e) {
            log.error("사용자를 찾을 수 없습니다.");
            throw new YOPLEServiceException(ApiStatusCode.USER_NOT_FOUND);
        }
        return userInfoEntities;
    }


}
