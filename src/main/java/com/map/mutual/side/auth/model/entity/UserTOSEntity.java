package com.map.mutual.side.auth.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class       : UserTOSEntity
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-06] - 조 준희 - Class Create
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "USER_TOS")
public class UserTOSEntity {

    @Id
    @Column(name = "SUID", nullable = false, columnDefinition = "VARCHAR(18)")
    private String suid;

    @Column(name = "SERVICE_TOS_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String serviceTosYN;

    @Column(name = "USER_INFO_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String userInfoYn;

    @Column(name = "LOCATION_INFO_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String locationInfoYn;

    @Column(name = "AGE_COLLECTION_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String ageCollectionYn;

    @Column(name = "MARKETING_YN", nullable = true, columnDefinition = "CHAR(1)")
    @ColumnDefault("'N'")
    private String marketingYn;

    @Builder
    public UserTOSEntity(String suid, String serviceTosYN, String userInfoYn, String locationInfoYn, String ageCollectionYn, String marketingYn) {
        this.suid = suid;
        this.serviceTosYN = serviceTosYN;
        this.userInfoYn = userInfoYn;
        this.locationInfoYn = locationInfoYn;
        this.ageCollectionYn = ageCollectionYn;
        this.marketingYn = marketingYn;
    }
}
