package com.map.mutual.side.review.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "PLACE")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PlaceEntity {
    @Id
    @Column(name="PLACE_ID", nullable = false, updatable = false, columnDefinition = "VARCHAR(20)")
    private String placeId;

    @Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(200)")
    private String name;

    @Column(name = "ADDRESS", nullable = false, columnDefinition = "VARCHAR(200)")
    private String address;

    @Column(name = "ROAD_ADDRESS", nullable = false, columnDefinition = "VARCHAR(200)")
    private String roadAddress;

    @Column(name = "CATEGORY_GROUP_CODE", nullable = false, columnDefinition = "VARCHAR(10)")
    private String categoryGroupCode;

    @Column(name = "CATEGORY_GROUP_NAME", nullable = false, columnDefinition = "VARCHAR(50)")
    private String categoryGroupName;

    @Column(name = "X", nullable = false, columnDefinition = "DECIMAL(16,13)")
    private BigDecimal x;

    @Column(name = "Y", nullable = false, columnDefinition = "DECIMAL(16,14)")
    private BigDecimal y;
}
