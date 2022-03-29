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
    @Transactional
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

            String worldCode = YOPLEUtils.getWorldRandomCode();
            WorldUserMappingEntity worldUserMappingEntity = WorldUserMappingEntity.builder()
                            .userSuid(userInfoDto.getSuid())
                                    .worldId(createWorld.getWorldId()).
                    worldUserCode(worldCode).
                    worldinvitationCode(worldCode).
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

    //4. 월드 수정하기
    @Override
    public void updateWorld(WorldDto worldDto) {
        try {
            // 1. 사용자 SUID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            // 2. 수정하려는 월드의 정보를 가져옴.
            //  수정하려는 월드가 없다면 Forbidden에러 발생 => 보안차원 (없는 월드의 ID를 알아낼 수 있으므로.)
            WorldEntity targetWorld = worldRepo.findById(worldDto.getWorldId())
                    .orElseThrow(() -> new YOPLEServiceException(ApiStatusCode.FORBIDDEN));

            targetWorld.updateWorldName(worldDto.getWorldName());
            targetWorld.updateWorldDesc(worldDto.getWorldDesc());

            //3. 월드의 생성자인지 확인.
            if(userInfoDto.getSuid().equals(targetWorld.getWorldOwner()) == false)
                throw new YOPLEServiceException(ApiStatusCode.FORBIDDEN);

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
    public WorldDetailResponseDto getWorldDetail(Long worldId, String suid) {
        try {

            WorldDetailResponseDto worldDetailResponseDto = worldRepo.getWorldDetail(worldId, suid);

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
