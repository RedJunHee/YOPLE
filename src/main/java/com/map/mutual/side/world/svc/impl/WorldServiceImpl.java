package com.map.mutual.side.world.svc.impl;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.auth.repository.WorldUserMappingRepo;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.utils.YOPLEUtils;
import com.map.mutual.side.world.model.dto.WorldDetailResponseDto;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.model.entity.WorldEntity;
import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
import com.map.mutual.side.world.repository.WorldRepo;
import com.map.mutual.side.world.svc.WorldService;
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

@Service
public class WorldServiceImpl implements WorldService {
    private final Logger logger = LogManager.getLogger(WorldServiceImpl.class);

    private WorldRepo worldRepo;
    private WorldUserMappingRepo worldUserMappingRepo;
    private ModelMapper modelMapper;
    private UserInfoRepo userInfoRepo;

    @Autowired
    public WorldServiceImpl(WorldRepo worldRepo
            , WorldUserMappingRepo worldUserMappingRepo
            , ModelMapper modelMapper
    , UserInfoRepo userInfoRepo) {
        this.worldRepo = worldRepo;
        this.worldUserMappingRepo = worldUserMappingRepo;
        this.modelMapper = modelMapper;
        this.userInfoRepo = userInfoRepo;
    }

    //1. 월드 생성하기.
    @Override
    //@Transactional
    public WorldDto createWolrd(WorldDto worldDto) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

//            WorldEntity createWorld = WorldEntity.builder().host(UserInfoEntity.builder().suid(userInfoDto.getSuid()).build())
            WorldEntity createWorld = WorldEntity.builder()
                    .worldName(worldDto.getWorldName())
                    .worldDesc(worldDto.getWorldDesc())
                    .worldOwner(userInfoDto.getSuid())
                    .build();

            worldRepo.save(createWorld);

            UserEntity userEntity = userInfoRepo.findById(userInfoDto.getSuid()).get();

            WorldUserMappingEntity worldUserMappingEntity = WorldUserMappingEntity.builder()
                            .userSuid(userInfoDto.getSuid())
                                    .worldId(createWorld.getWorldId()).
                    worldUserCode(YOPLEUtils.getWorldRandomCode()).
                    build();

            worldUserMappingRepo.save(worldUserMappingEntity);




            WorldDto createdWorld = WorldDto.builder().worldId(createWorld.getWorldId())
                    .worldDesc(createWorld.getWorldDesc())
                    .worldName(createWorld.getWorldName())
                    .build();

            return createdWorld;

        } catch (Exception e) {
            logger.error("World Create Failed.!! : " + e.getMessage());
            throw e;

        }
    }


    //3. 월드 초대 수락하기.
    @Override
    public WorldDto inviteJoinWorld(WorldDto worldDto) {

        try {

            // 1. 사용자 SUID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            // 2. 사용자가 월드에 이미 가입 되어있는지 확인.
//            if (worldUserMappingRepo.findByUserInfoEntityAndWorldEntity(modelMapper.map(userInfoDto, UserEntity.class), modelMapper.map(worldDto, WorldEntity.class)).stream().count() != 0) {
            // TODO: 2022/03/25 수정한 api확인
            if (worldUserMappingRepo.findByUserEntityAndWorldEntity(userInfoDto.getUserId(), worldDto.getWorldId()).stream().count() != 0) {
                logger.error("해당 사용자가 이미 월드에 속해있습니다.");
                //TODO 해당 사용자가 월드에 속해있을때 파라미터 체크 오류로 나가고 있음.
                throw new YOPLEServiceException(ApiStatusCode.PARAMETER_CHECK_FAILED);

            }

            // 3. 초대 수락한 월드 입장 처리
            WorldUserMappingEntity worldUserMappingEntity = WorldUserMappingEntity.builder()
//                    .userInfoEntity(modelMapper.map(userInfoDto, UserInfoEntity.class))
                    .worldEntity(modelMapper.map(worldDto, WorldEntity.class))
                    .build();

            worldUserMappingRepo.save(worldUserMappingEntity);

            // 4. 참여한 월드 정보 조회
            WorldEntity world = worldRepo.findById(worldUserMappingEntity.getWorldEntity().getWorldId())
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

    //4. 월드 수정하기
    @Override
    public void updateWorld(WorldDto worldDto) {
        try {
            // 1. 사용자 SUID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            // 2. 수정하려는 월드가 자신의 월드인지 확인.  아니라면 Exception
            WorldEntity targetWorld = worldRepo.findById(worldDto.getWorldId())
                    .orElseThrow(() -> new YOPLEServiceException(ApiStatusCode.FORBIDDEN));

            targetWorld.updateWorldName(worldDto.getWorldName());
            targetWorld.updateWorldDesc(worldDto.getWorldDesc());

            worldRepo.save(targetWorld);

        } catch (YOPLEServiceException e) {
            logger.error("World updateWorld Failed.!! : " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("World updateWorld Failed.!! : " + e.getMessage());
            throw e;
        }
    }

    //4. 월드 상세정보 조회
    @Override
    public WorldDetailResponseDto getWorldDetail(Long worldId, UserInfoDto requestUser) {
        try {

            WorldDetailResponseDto worldDetailResponseDto = worldRepo.getWorldDetail(worldId, requestUser);

            return worldDetailResponseDto;

        } catch (Exception e) {
            logger.error("World getWorldDetail Failed.!! : " + e.getMessage());
            throw e;
        }
    }

    //5. 월드 참여자 리스트 조회
    @Override
    public List<WorldDto> getWorldList(String suid) {

        try {
            List<WorldDto> activityWorlds = worldUserMappingRepo.findBySuidWithWorld(suid);

            return activityWorlds;
        } catch (Exception e) {
            throw e;
        }
    }
}
