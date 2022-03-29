package com.map.mutual.side.world.svc;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.world.model.dto.WorldDetailResponseDto;
import com.map.mutual.side.world.model.dto.WorldDto;
import com.map.mutual.side.world.model.entity.WorldEntity;

import java.util.List;

public interface WorldService {
    //1. 월드 생성하기.
    WorldDto createWolrd(WorldDto worldDto);


    //3. 월드 수정하기
    void updateWorld(WorldDto worldDto);

    // 4. 월드 상세 정보 조회
    WorldDetailResponseDto getWorldDetail(Long worldDto, String suid);

    //4. 참여 월드 리스트 조회
    List<WorldDto> getWorldList(String suid);

}
