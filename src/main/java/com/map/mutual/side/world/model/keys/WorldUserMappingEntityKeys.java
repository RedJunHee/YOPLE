package com.map.mutual.side.world.model.keys;

import lombok.EqualsAndHashCode;

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
