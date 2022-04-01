package com.map.mutual.side.common.fcmmsg.model.entity;

import com.map.mutual.side.common.fcmmsg.model.keys.FcmTopicKeys;
import lombok.*;

import javax.persistence.*;

/**
 * fileName       : FcmTopicEntity
 * author         : kimjaejung
 * createDate     : 2022/03/29
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/29        kimjaejung       최초 생성
 *
 */
@Entity
@Table(name = "FCM_TOPIC")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@IdClass(FcmTopicKeys.class)
public class FcmTopicEntity {
    @Id
    @Column(name="FCM_TOKEN", columnDefinition = "VARCHAR(170)")
    private String fcmToken;

    @Id
    @Column(name="WORLD_ID", columnDefinition = "BIGINT")
    private Long worldId;
}
