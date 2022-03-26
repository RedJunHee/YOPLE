package com.map.mutual.side.auth.svc;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.UserEntity;

import java.util.List;

/**
 * fileName       : UserService
 * author         : kimjaejung
 * createDate     : 2022/03/16
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/16        kimjaejung       최초 생성
 *
 */
public interface UserService {
    UserInfoDto getUserById(String id);
    UserInfoDto getUserByPhone(String phone);
    UserEntity findById(String suid);
    List<UserInfoDto> getUsers(long worldId);
}
