package com.map.mutual.side.world.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.world.model.dto.WorldAuthResponseDto;
import com.map.mutual.side.world.model.dto.WorldDetailResponseDto;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.svc.WorldService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/world")
@Validated
public class WorldController {
    private final Logger logger = LogManager.getLogger(WorldController.class);
    private WorldService worldService;

    @Autowired
    public WorldController(WorldService worldService) {
        this.worldService = worldService;
    }

    /**
     * Description : 1. 월드 생성하기.
     * - 월드 생성 제한 초과시 250 EXCEEDED_LIMITED_COUNT 예외.
     * Name        : createWorld
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PostMapping(value = "/world")
    public ResponseEntity<ResponseJsonObject> createWorld(@RequestBody WorldDto worldDto) throws YOPLEServiceException, FirebaseMessagingException {
        try{
            // 1. 월드 바로 생성하기.
            WorldDto createdWorld = worldService.createWolrd(worldDto);

            // 2. 응답 생성.
            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(createdWorld);

            // 3. 리턴.
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.debug("월드 생성하기 Exception : {}", e.getResponseJsonObject().getMeta().toString());
            throw e;
        }catch(Exception e){
            logger.error("월드 생성하기 ERROR : {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 2. 월드 수정하기.
     * Name        : updateWorld
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @PatchMapping(value = "/world")
    public ResponseEntity<ResponseJsonObject> updateWorld(@RequestBody WorldDto worldDto) throws YOPLEServiceException {
        try{

            // 1. 월드 수정하기.
            worldService.updateWorld(worldDto);

            // 2. 응답 생성.
            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            // 3. 리턴.
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.debug("월드 수정하기 Exception : {}", e.getResponseJsonObject().getMeta().toString());
            throw e;
        }catch(Exception e){
            logger.error("월드 수정하기 ERROR : {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 3. 월드 상세정보 조회.
     * - 존재하지 않는 월드 조회 시 권한 없음 403 에러
     * Name        : worldDetail
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping(value = "/world")
    public ResponseEntity<ResponseJsonObject> worldDetail(@NotNull @RequestParam Long worldId) throws YOPLEServiceException {
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

        }catch(Exception e){
            logger.error("월드 상세정보 조회 ERROR : {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 4. 참여 중인 월드 조회.
     * - isDetails로 세부정보 조회 가능.
     * Name        : activityWorlds
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping(value = "/user/worlds")
    public ResponseEntity<ResponseJsonObject> activityWorlds(@RequestParam(value = "isDetails", required = false, defaultValue = "N") @Valid @Pattern(regexp = "Y|N") String isDetails){
        try{

            // 1. 사용자 SUID 가져오기.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            // 2. 참여 중인 월드 리스트 조회하기.
            List<WorldDto> activityWorldDtoList = worldService.getWorldList(userInfoDto.getSuid(), isDetails);

            logger.debug("현재 참여 중인 월드 카운트 : {}", activityWorldDtoList.stream().count());

            // 3. 응답 객체 생성.
            Map<String, Object> responseObj = new HashMap<>();
            responseObj.put("activityWorlds",activityWorldDtoList);
            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(responseObj);

            // 4. 리턴.
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(Exception e){
            logger.error("참여 중인 월드 조회 ERROR : {}", e.getMessage());
            throw e;
        }

    }

    /**
     * Description : 5. 리뷰가 등록된 월드 조회 - 월드 이름만 나옴
     * Name        : getWorldOfReivew
     * Author      : 조 준 희
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
            logger.debug("리뷰가 등록된 월드 카운트 : {}",worlds.stream().count());

            // 3. 응답 설정.
            Map<String, Object> response = new HashMap<>();
            response.put("worlds",worlds);
            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(response);

            // 4. 리턴.
            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
        }catch(Exception e){
            logger.error("리뷰가 등록된 월드 조회 ERROR : {}", e.getMessage());
            throw e;
        }

    }

    /**
     * Description : 6. 월드 초대 코드 유효성 체크
     *  - 월드 초대 코드가 유효한 코드인지 확인.
     * Name        : worldUserCodeValid
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping(value = "/code-validation")
    public ResponseEntity<ResponseJsonObject> worldUserCodeValid(@RequestParam("worldUserCode") String worldUserCode) throws YOPLEServiceException {
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
            logger.debug("월드 초대 코드 유효성 체크 Exception : {}", e.getResponseJsonObject().getMeta().toString());
            throw e;
        }catch(Exception e){
            logger.error("월드 초대 코드 유효성 체크 ERROR : {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Description : 7. 월드에 입장 권한 체크.
     * Name        : worldAuthCheck
     * Author      : 조 준 희
     * History     : [2022-04-06] - 조 준 희 - Create
     */
    @GetMapping(value = "/user/auth-check")
    public ResponseEntity<ResponseJsonObject> worldAuthCheck(@RequestParam("worldId") Long worldId) throws YOPLEServiceException {
        try {

            // 1. 사용자 SUID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            ResponseJsonObject responseJsonObject;

            WorldAuthResponseDto worldAuthResponseDto = null;

            // 2. 월드에 참여 중인지 확인 후 응답 설정.
            if ((worldAuthResponseDto = worldService.authCheck(worldId, userInfoDto.getSuid())) != null)
                responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(worldAuthResponseDto);
            else
                throw new YOPLEServiceException(ApiStatusCode.FORBIDDEN,"월드 입장 권한 없음.");

            // 3. 리턴.
            return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);

        }catch(YOPLEServiceException e){
            logger.debug("월드 입장 권한 체크 Exception : {}", e.getResponseJsonObject().getMeta().toString());
            throw e;
        }catch(Exception e){
            logger.error("월드 입장 권한 체크 ERROR : {}", e.getMessage());
            throw e;
        }

    }


}
