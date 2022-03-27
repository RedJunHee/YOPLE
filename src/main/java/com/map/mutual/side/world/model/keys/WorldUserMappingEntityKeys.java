package com.map.mutual.side.world.model.keys;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.world.model.entity.WorldEntity;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import java.io.Serializable;
/**
 * fileName       : WorldUserMappingEntityKeys
 * author         : kimjaejung
 * createDate     : 2022/03/25
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/25        kimjaejung       최초 생성
 *
 */
@EqualsAndHashCode
public class WorldUserMappingEntityKeys implements Serializable {
    private String userSuid;
    private Long worldId;
}
