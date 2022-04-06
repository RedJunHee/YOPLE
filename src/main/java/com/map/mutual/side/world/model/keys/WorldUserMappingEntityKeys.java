package com.map.mutual.side.world.model.keys;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.world.model.entity.WorldEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class WorldUserMappingEntityKeys implements Serializable {
    private String userSuid;
    private Long worldId;

    @Builder
    public WorldUserMappingEntityKeys(String userSuid, Long worldId) {
        this.userSuid = userSuid;
        this.worldId = worldId;
    }
}
