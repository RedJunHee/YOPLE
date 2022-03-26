package com.map.mutual.side.world.model.keys;

import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import java.io.Serializable;
/**
 * fileName       : WorldEntityKeys
 * author         : kimjaejung
 * createDate     : 2022/03/25
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/25        kimjaejung       최초 생성
 *
 */
@EqualsAndHashCode
public class WorldEntityKeys implements Serializable {
    private Long worldId;
    private String worldOwner;

}
