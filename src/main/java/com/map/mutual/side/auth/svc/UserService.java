package com.map.mutual.side.auth.svc;

import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.UserEntity;

import java.util.List;
import java.util.Optional;

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
    UserInfoDto findUser(String id, String phone);
    UserEntity findById(String suid);
    List<UserInWorld> worldUsers(long worldId);
}
