package com.map.mutual.side.auth.svc;

import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.world.model.dto.WorldDto;

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
    List<UserInWorld> worldUsers(long worldId);
    //2. 월드 초대 수락하기.
    WorldDto inviteJoinWorld( String worldinvitationCode);
    UserInfoDto userDetails(String suid);
    UserInfoDto userInfoUpdate(String suid, String userId, String profileUrl);
    void userLogout(String suid);
    UserInfoDto signUp(UserInfoDto user) throws Exception;
    void userWorldInviting(String suid, String targetSuid, Long worldId);

}
