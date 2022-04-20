package com.map.mutual.side.world.model.entity;

import com.map.mutual.side.common.repository.config.CreateDtEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Class       : WorldJoinLogEntity
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-20] - 조 준희 - Class Create
 */
@Entity
@Table(name = "WORLD_JOIN_LOG")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorldJoinLogEntity extends CreateDtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private Long seq;

    @Column(name = "USER_SUID", nullable = false, columnDefinition = "VARCHAR(18)")
    private String userSuid;

    @Column(name = "WORLD_ID")
    private Long worldId;




}
