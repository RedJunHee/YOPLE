package com.map.mutual.side.world.svc.impl;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.repository.UserInfoRepo;
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


    /**
     * Name        : createWolrd
     * Author      : 조 준 희
     * Description : 월드 생성하기.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    @Transactional
    public WorldDto createWolrd(WorldDto worldDto) {

        // 1. 사용자 SUID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

        // 2. 생성할 WorldEntity 설정.
        WorldEntity createWorld = WorldEntity.builder()
                .worldName(worldDto.getWorldName())
                .worldDesc(worldDto.getWorldDesc())
                .worldOwner(userInfoDto.getSuid())
                .build();

        // 3. 월드 생성.
        worldRepo.save(createWorld);

        // 4. 월드 코드 생성.
        String worldCode = YOPLEUtils.getWorldRandomCode();

        // 5. 월드 매핑 설정.
        WorldUserMappingEntity worldUserMappingEntity = WorldUserMappingEntity.builder()
                        .userSuid(userInfoDto.getSuid())
                        .worldId(createWorld.getWorldId())
                        .worldUserCode(worldCode)
                        .worldinvitationCode(worldCode)
                        .accessTime(LocalDateTime.now()).
        build();

        // 6. 월드 매핑 저장.
        worldUserMappingRepo.save(worldUserMappingEntity);

        // 7. 생성된 월드 정보 DTO 생성.
        WorldDto createdWorld = WorldDto.builder().worldId(createWorld.getWorldId())
                .worldDesc(createWorld.getWorldDesc())
                .worldName(createWorld.getWorldName())
                .build();

        // 8. 리턴.
        return createdWorld;

    }

    /**
     * Name        : updateWorld
     * Author      : 조 준 희
     * Description : 월드 수정하기.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public void updateWorld(WorldDto worldDto) throws YOPLEServiceException {

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

    }

    /**
     * Name        : getWorldDetail
     * Author      : 조 준 희
     * Description : 월드 상세정보 조회
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public WorldDetailResponseDto getWorldDetail(Long worldId, String suid) {

        // 1. 월드 상세정보 조회 - suid 생성자와 같다면 마이월드.
        WorldDetailResponseDto worldDetailResponseDto = worldRepo.getWorldDetail(worldId, suid);

        // 2. 리턴.
        return worldDetailResponseDto;

    }

    /**
     * Name        : getWorldList
     * Author      : 조 준 희
     * Description : 참여 중인 월드 리스트 조회
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public List<WorldDto> getWorldList(String suid, String isDetails) {

        List<WorldDto> activityWorlds ;

        // 1. isDetails 여부에 따라 월드 상세정보 조회 분기.
        if(isDetails.equals("Y"))
            activityWorlds = worldUserMappingRepo.findBySuidWithWorldDetails(suid);
        else
            activityWorlds = worldUserMappingRepo.findBySuidWithWorld(suid);

        // 2. 리턴.
        return activityWorlds;

    }

    /**
     * Name        : authCheck
     * Author      : 조 준 희
     * Description : 월드에 입장 권한이 있는지 확인.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public Boolean authCheck(Long worldId, String suid) {

        // 1. 월드 매핑 정보 조회
        Optional<WorldUserMappingEntity> worldUserMappingEntity
                = worldUserMappingRepo.findByWorldIdAndUserSuid(worldId,suid);

        // 월드 소속 멤버 인경우
        if(worldUserMappingEntity.isPresent() == true){
           WorldUserMappingEntity mapping = worldUserMappingEntity.get();
           // 월드 입장 시간 갱신.
           mapping.setAccessTime(LocalDateTime.now());
           worldUserMappingRepo.save(mapping);

            return true;    // 월드에 참여중이므로 입장 가능.
        }
        else    // 월드에 참여되어있지 않음. => 월드 입장 권한 없음.
            return false;
    }

    /**
     * Name        : getWorldOfReivew
     * Author      : 조 준 희
     * Description : 리뷰가 등록된 월드 리스트 조회하기.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public List<WorldDto> getWorldOfReivew(Long reviewId, String suid) {

        // 1. 리뷰가 등록된 월드 리스트 조회하기.
        List<WorldDto> worlds = reviewWorldMappingRepo.findAllWorldsByReviewId(reviewId, suid);

        // 2. 리턴.
        return worlds;

    }

    /**
     * Name        : worldUserCodeValid
     * Author      : 조 준 희
     * Description : 월드 코드 유효성 체크.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @Override
    public Boolean worldUserCodeValid(String worldUserCode) throws YOPLEServiceException {
            worldUserMappingRepo.findOneByWorldUserCode(worldUserCode)
                    .orElseThrow(()-> new YOPLEServiceException(ApiStatusCode.WORLD_USER_CDOE_VALID_FAILED));

            return true;

    }
}
