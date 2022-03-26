package com.map.mutual.side.message.model.entity;

import com.map.mutual.side.auth.model.entity.UserEntity;
import lombok.*;

import javax.persistence.*;

/**
 * fileName       : FcmTokenEntity
 * author         : kimjaejung
 * createDate     : 2022/03/25
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/25        kimjaejung       최초 생성
 *
 */
@Entity
@Table(name = "FCM_TOKEN")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FcmTokenEntity {
    @Id
    @Column(name="USER_SUID", insertable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Column(name="FCM_TOKEN", columnDefinition = "VARCHAR(200)")
    private String title;

}
