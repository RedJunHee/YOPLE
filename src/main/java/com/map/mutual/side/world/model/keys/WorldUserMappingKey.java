package com.map.mutual.side.world.model.keys;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.world.model.entity.WorldEntity;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * fileName       : WorldUserMappingKey
 * author         : kimjaejung
 * createDate     : 2022/03/25
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/25        kimjaejung       최초 생성
 *
 */
@EqualsAndHashCode
public class WorldUserMappingKey implements Serializable {
    private WorldEntity worldEntity;
    private UserEntity userEntity;
}
