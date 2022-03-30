package com.map.mutual.side.world.controller;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.svc.UserService;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
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
    private UserService userService;

    @Autowired
    public WorldController(WorldService worldService, UserService userService) {
        this.userService = userService;
        this.worldService = worldService;
    }

    /** 월드 생성하기 API */
    @PostMapping(value = "/world")
    public ResponseEntity<ResponseJsonObject> createWorld(@RequestBody WorldDto worldDto){
        try{
           WorldDto createdWorld = worldService.createWolrd(worldDto);

           ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(createdWorld);

           return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(Exception e)
        {
            logger.error("WorldController createWorld Failed.!! : "+ e.getMessage());
            throw e;
        }
    }

    /** 월드 수정하기 API */
    @PatchMapping(value = "/world")
    public ResponseEntity<ResponseJsonObject> updateWorld(@RequestBody WorldDto worldDto){
        try{

            worldService.updateWorld(worldDto);

            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(Exception e)
        {
            logger.error("WorldController updateWorld Failed.!! : "+ e.getMessage());
            throw e;
        }
    }

    /** 월드 상세 정보 보기 API */
    @GetMapping(value = "/world")
    public ResponseEntity<ResponseJsonObject> worldDetail(@NotNull @RequestParam Long worldId){
        try{
            WorldDetailResponseDto worldDetail ;

            // 1. 사용자 SUID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();


            worldDetail = worldService.getWorldDetail(worldId,userInfoDto.getSuid());


            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(worldDetail);

            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(Exception e)
        {
            logger.error("WorldController updateWorld Failed.!! : "+ e.getMessage());
            throw e;
        }
    }

    //참여 중인 월드 리스트 조회
    @GetMapping(value = "/user/worlds")
    public ResponseEntity<ResponseJsonObject> activityWorlds(@RequestParam("isDetails") String isDetails){

        try{
            WorldDetailResponseDto worldDetail ;
            String deatilsYN = StringUtil.isNullOrEmpty(isDetails)? "N": isDetails;

            // 1. 사용자 SUID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();

            List<WorldDto> activityWorldDtoList = worldService.getWorldList(userInfoDto.getSuid(), deatilsYN);

            Map<String, Object> responseObj = new HashMap<>();

            responseObj.put("activityWorlds",activityWorldDtoList);

            ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.OK).setData(responseObj);

            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch(Exception e)
        {
            logger.error("WorldController updateWorld Failed.!! : "+ e.getMessage());
            throw e;
        }

    }

}
