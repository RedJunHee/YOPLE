package com.map.mutual.side.world.svc.impl;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.map.mutual.side.review.model.keys.ReviewWorldMappingEntityKeys;
import com.map.mutual.side.review.repository.ReviewWorldMappingRepository;
import com.map.mutual.side.world.repository.WorldUserMappingRepo;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorldServiceImpl implements WorldService {
    private final Logger logger = LogManager.getLogger(WorldServiceImpl.class);

    private WorldRepo worldRepo;
    private WorldUserMappingRepo worldUserMappingRepo;
    private ModelMapper modelMapper;
    private UserInfoRepo userInfoRepo;
    private ReviewWorldMappingRepository reviewWorldMappingRepo;

    @Autowired
    public WorldServiceImpl(WorldRepo worldRepo, WorldUserMappingRepo worldUserMappingRepo
            , ModelMapper modelMapper, UserInfoRepo userInfoRepo
            , ReviewWorldMappingRepository reviewWorldMappingRepo) {
        this.worldRepo = worldRepo;
        this.worldUserMappingRepo = worldUserMappingRepo;
        this.modelMapper = modelMapper;
        this.userInfoRepo = userInfoRepo;
        this.reviewWorldMappingRepo = reviewWorldMappingRepo;
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
                            .worldId(createWorld.getWorldId())
                            .worldUserCode(worldCode)
                            .worldinvitationCode(worldCode)
                            .accessTime(LocalDateTime.now()).
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

    //5. 참여 중인 월드 리스트 조회
    @Override
    public List<WorldDto> getWorldList(String suid, String isDetails) {

        try {
            List<WorldDto> activityWorlds ;

            if(isDetails.equals("Y"))
                activityWorlds = worldUserMappingRepo.findBySuidWithWorldDetails(suid);
                else
                activityWorlds = worldUserMappingRepo.findBySuidWithWorld(suid);

            return activityWorlds;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Boolean authCheck(Long worldId, String suid) {

        try{
            Optional<WorldUserMappingEntity> worldUserMappingEntity = worldUserMappingRepo.findByWorldIdAndUserSuid(worldId,suid);
            if(worldUserMappingEntity.isPresent() == true){
                // 월드 소속 멤버 인경우

               WorldUserMappingEntity mapping = worldUserMappingEntity.get();

               mapping.setAccessTime(LocalDateTime.now());
               worldUserMappingRepo.save(mapping);

                return true;
            }
            else
                return false;

        }catch(YOPLEServiceException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
    }

    //리뷰가 등록된 월드 리스트 조회하기.
    @Override
    public List<WorldDto> getWorldOfReivew(Long reviewId, String suid) {
        try{

            List<WorldDto> worlds = reviewWorldMappingRepo.findAllWorldsByReviewId(reviewId, suid);

            return worlds;
        }catch(YOPLEServiceException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
    }

    // 월드 초대 코드 유효성 체크
    @Override
    public Boolean worldUserCodeValid(String worldUserCode) {
        try{
            worldUserMappingRepo.findByWorldUserCode(worldUserCode)
                    .orElseThrow(()-> new YOPLEServiceException(ApiStatusCode.WORLD_USER_CDOE_VALID_FAILED));

            return true;

        }catch(YOPLEServiceException e){
            logger.debug("월드 초대 코드 유효성 체크 실패.");
            throw e;
        }catch(Exception e){
            throw e;
        }
    }
}
