package com.map.mutual.side.world.controller;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.svc.UserService;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.world.model.dto.WorldDetailResponseDto;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.svc.WorldService;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/world")
public class WorldController {
    private final Logger logger = LogManager.getLogger(WorldController.class);
    private WorldService worldService;

    @Autowired
    public WorldController(WorldService worldService) {
        this.worldService = worldService;
    }

    /**
     * Name        : createWorld
     * Author      : 조 준 희
     * Description : 월드 생성하기.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PostMapping(value = "/world")
    public ResponseEntity<ResponseJsonObject> createWorld(@RequestBody WorldDto worldDto){
        try{
            // TODO: 2022-04-06 월드 생성 시 벨리데이션 체크 필요한지 생각하기.

            // 1. 월드 바로 생성하기.
            WorldDto createdWorld = worldService.createWolrd(worldDto);

            // 2. 응답 생성.
            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(createdWorld);

            // 3. 리턴.
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.debug(e.getMessage());
            throw e;
        }catch(Exception e)
        {
            logger.error("WorldController createWorld Failed.!! : "+ e.getMessage());
            throw e;
        }
    }

    /**
     * Name        : updateWorld
     * Author      : 조 준 희
     * Description : 월드 수정하기.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PatchMapping(value = "/world")
    public ResponseEntity<ResponseJsonObject> updateWorld(@RequestBody WorldDto worldDto){
        try{

            // 1. 월드 수정하기.
            worldService.updateWorld(worldDto);

            // TODO: 2022-04-06 월드 수정 후 수정 된 월드 정보를 응답 안해주고 있음. 체크 필요.

            // 2. 응답 생성.
            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);


            // 3. 리턴.
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.debug(e.getMessage());
            throw e;
        }catch(Exception e)
        {
            logger.error("WorldController updateWorld Failed.!! : "+ e.getMessage());
            throw e;
        }
    }

    /**
     * Name        : worldDetail
     * Author      : 조 준 희
     * Description : 월드 상세정보 조회.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping(value = "/world")
    public ResponseEntity<ResponseJsonObject> worldDetail(@NotNull @RequestParam Long worldId){
        try{
            WorldDetailResponseDto worldDetail ;

            // 1. 사용자 SUID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            // 2. 월드 상세정보 조회하기.
            worldDetail = worldService.getWorldDetail(worldId,userInfoDto.getSuid());


            // 3. 응답 생성.
            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(worldDetail);

            // 4. 리턴.
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.debug(e.getMessage());
            throw e;
        }catch(Exception e)
        {
            logger.error("WorldController updateWorld Failed.!! : "+ e.getMessage());
            throw e;
        }
    }

    //참여 중인 월드 리스트 조회
    /**
     * Name        : activityWorlds
     * Author      : 조 준 희
     * Description : 참여 중인 월드 리스트 조회. isDetails로 세부정보 조회 가능.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping(value = "/user/worlds")
    public ResponseEntity<ResponseJsonObject> activityWorlds(@RequestParam("isDetails") String isDetails){
        try{
            WorldDetailResponseDto worldDetail ;

            // 1. isDetails 기본 값 설정.
            String deatilsYN = StringUtil.isNullOrEmpty(isDetails)? "N": isDetails;

            // 2. 사용자 SUID 가져오기.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            // 3. 참여 중인 월드 리스트 조회하기.
            List<WorldDto> activityWorldDtoList = worldService.getWorldList(userInfoDto.getSuid(), deatilsYN);


            // 4. 응답 객체 생성.
            Map<String, Object> responseObj = new HashMap<>();
            responseObj.put("activityWorlds",activityWorldDtoList);
            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(responseObj);

            // 5. 리턴.
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.debug(e.getMessage());
            throw e;
        }catch(Exception e)
        {
            logger.error("WorldController updateWorld Failed.!! : "+ e.getMessage());
            throw e;
        }

    }

    /**
     * Name        : worldAuthCheck
     * Author      : 조 준 희
     * Description : 월드에 입장이 가능한지 권한 체크.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping(value = "/user/auth-check")
    public ResponseEntity<ResponseJsonObject> worldAuthCheck(@RequestParam("worldId") Long worldId){
        try{

            // 1. 사용자 SUID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            ResponseJsonObject responseJsonObject;

            // 2. 월드에 참여 중인지 확인 후 응답 설정.
            if(worldService.authCheck(worldId, userInfoDto.getSuid()) == true )
                responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            else
                responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.FORBIDDEN);

            // 3. 리턴.
            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.debug(e.getMessage());
            throw e;
        }catch(Exception e){
            throw e;
        }

    }

    /**
     * Name        : getWorldOfReivew
     * Author      : 조 준 희
     * Description : 리뷰가 등록된 월드 리스트 조회 - 월드 이름만 나옴
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping(value = "/review/worlds")
    public ResponseEntity<ResponseJsonObject> getWorldOfReivew(@RequestParam("reviewId") Long reviewId){
        try{

            ResponseJsonObject responseJsonObject;

            // 1. 사용자 SUID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            // 2. 리뷰가 등록된 월드 리스트 조회.
            List<WorldDto> worlds = worldService.getWorldOfReivew(reviewId, userInfoDto.getSuid());

            // 3. 응답 설정.
            Map<String, Object> response = new HashMap<>();
            response.put("worlds",worlds);
            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(response);

            // 4. 리턴.
            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
        }catch(YOPLEServiceException e){
            logger.debug(e.getMessage());
            throw e;
        }catch(Exception e){
            throw e;
        }

    }

    /**
     * Name        : worldUserCodeValid
     * Author      : 조 준 희
     * Description : 월드 초대 코드 유효성 체크 - 월드 초대 코드가 유효한 코드인지 확인.
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping(value = "/code-validation")
    public ResponseEntity<ResponseJsonObject> worldUserCodeValid(@RequestParam("worldUserCode") String worldUserCode){
        try{
            ResponseJsonObject responseJsonObject;

            // 1. 월드 코드가 존재한 월드 코드인지 확인.
            //유효성 실패 시 throw YOPLEServiceException
            worldService.worldUserCodeValid(worldUserCode);

            // 2. 응답 생성.
            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            // 3. 리턴.
            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.debug("월드 초대 코드 유효성 체크 실패.");
            throw e;
        }catch(Exception e){
            throw e;
        }
    }

}
