package com.map.mutual.side.world.svc;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.world.model.dto.WorldAuthResponseDto;
import com.map.mutual.side.world.model.dto.WorldDetailResponseDto;
import com.map.mutual.side.world.model.dto.WorldDto;

import java.util.List;

public interface WorldService {

    void updateWorld(WorldDto worldDto) throws YOPLEServiceException;    // 월드 수정하기

    WorldDto createWolrd(WorldDto worldDto) throws YOPLEServiceException, FirebaseMessagingException;    // 월드 생성하기.

    List<WorldDto> getWorldList(String suid, String isDetails); // 참여 중인 월드 리스트 조회
    List<WorldDto> getWorldOfReivew (Long reviewId, String suid);   // 리뷰가 등록된 월드 조회

    WorldDetailResponseDto getWorldDetail(Long worldDto, String suid) throws YOPLEServiceException; // 월드 상세 정보 조회

    WorldAuthResponseDto authCheck(Long worldId, String suid) throws YOPLEServiceException;   // 월드 입장 권한 체크
    Boolean worldUserCodeValid(String worldUserCode) throws YOPLEServiceException;   // 월드 코드 유효성 체크.
}
