package com.map.mutual.side.auth.model.entity;

import com.map.mutual.side.common.repository.config.CreateDtEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.time.LocalDateTime;

/** Class       : User (Model)
 *  Author      : 조 준 희
 *  Description : USER_INFO 테이블에 매필될 Model
 *  History     : [2022-03-11] - TEMP
 */
@Table(name= "USER_INFO")
@Getter
@Setter
@Entity
@DynamicUpdate
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends CreateDtEntity implements Persistable<String> {

    @Id
    @Column(name="SUID", nullable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String suid;

    @Column(name="USER_ID", nullable = false, columnDefinition = "VARCHAR(20)")
    private String userId;//닉네임

    // 한글 10자
    @Column(name="NAME", nullable = false, columnDefinition = "VARCHAR(10)")
    private String name;

    @Column(name="PHONE", nullable = false, columnDefinition = "VARCHAR(15)")
    private String phone;

    @Column(name="PROFILE_URL", columnDefinition = "VARCHAR(100)")
    private String profileUrl;

    @Column(name="PROFILE_PIN_URL", columnDefinition = "VARCHAR(100)")
    private String profilePinUrl;

    @Column(name="FCM_TOKEN", columnDefinition = "VARCHAR(170)")
    private String fcmToken;

    @Column(name="NOTI_CHECK_DT", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime notiCheckDt ;

    public void updateNotiCheckDt()
    {
        notiCheckDt = LocalDateTime.now();
    }

    @Override
    public String getId() {
        return getSuid();
    }

    @Override
    public boolean isNew() {
        return getCreateTime() == null;
    }
}
