package com.map.mutual.side.auth.svc.impl;

import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.auth.repository.UserInfoRepo;
import com.map.mutual.side.auth.repository.WorldUserMappingRepo;
import com.map.mutual.side.auth.svc.UserService;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private WorldUserMappingRepo worldUserMappingRepo;
    @Autowired
    private UserInfoRepo userInfoRepo;

    @Autowired
    private ModelMapper modelMapper;


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

    @Override
    public UserEntity findById(String suid) {

        try {
            UserEntity userEntity = userInfoRepo.findById(suid)
                    .orElse(UserEntity.builder().build());

            return userEntity;

        } catch (Exception e) {
            throw e;
        }
    }


}
