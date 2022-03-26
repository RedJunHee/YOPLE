package com.map.mutual.side.world.repository;

import com.map.mutual.side.world.model.entity.WorldEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

/**
 * Class       : WorldRepoTest
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-18] - 조 준희 - Class Create
 */

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WorldRepoTest {

    @Autowired
    private WorldRepo worldRepo;

    @Test
    @DisplayName("월드 생성하기 Repository")
    void save()
    {
        //given
        WorldEntity createWorld = WorldEntity.builder().worldName("준희나라, 월드 월드")
                .worldDesc("준희나라 입니다.~")
//                .host(UserInfoEntity.builder().suid("Dev_Test").build())
                .build();

        //when
        worldRepo.save(createWorld);

        //then
        Assertions.assertEquals(createWorld.getWorldName(),"준희나라, 월드 월드");
        Assertions.assertEquals(createWorld.getWorldDesc(),"준희나라 입니다.~");

    }

}
