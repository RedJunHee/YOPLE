package com.map.mutual.side.auth.model.entity;

import com.map.mutual.side.auth.model.keys.JWTRefreshTokenLogEntityKeys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

/**
 * Class       : JWTrefreshToken
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-14] - 조 준희 - Class Create
 */
@Entity
@Table(name = "JWT_REFRESH_TOKEN_LOG")
@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class JWTRefreshTokenLogEntity implements Persistable<String> {

    @Id
    @Column(name="USER_SUID", insertable = false, updatable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Column(name = "REFRESH_TOKEN", nullable = false, columnDefinition = "VARCHAR(300)")
    private String refreshToken;

    @Transient
    private boolean isPersist = false;

    @PostLoad
    public void isPersist(){
        isPersist = true;
    }

    @Override
    public String getId() {
        return getUserSuid();
    }

    @Override
    public boolean isNew() {
        return (isPersist==false);
    }
}
