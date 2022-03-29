//package com.map.mutual.side.auth.repository;
//
//import com.map.mutual.side.world.model.entity.WorldEntity;
//import com.map.mutual.side.world.model.entity.WorldUserMappingEntity;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
///**
// * Class       : WorldUserMappingRepoTest
// * Author      : 조 준 희
// * Description : Class Description
// * History     : [2022-03-18] - 조 준희 - Class Create
// */
//@ExtendWith(SpringExtension.class)
//@DataJpaTest
//@ActiveProfiles("local")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class WorldUserMappingRepoTest {
//
//    @Autowired
//    WorldUserMappingRepo worldUserMappingRepo;
//
//
//    @Test
//    @DisplayName("월드-사용자 매핑 (월드 입장)")
//    void inviteJoinWorld()
//    {
////        try {
////            WorldUserMappingEntity worldUserMappingEntity = WorldUserMappingEntity.builder()
////                    .worldEntity(WorldEntity.builder().worldId(1L).build())
//////                    .userInfoEntity(UserInfoEntity.builder().suid("TESTCODESUID").build())
////                    .build();
////
////            worldUserMappingRepo.save(worldUserMappingEntity);
////
////            Assertions.assertEquals(worldUserMappingEntity.getWorldEntity().getWorldId(), 1l);
//////            Assertions.assertEquals(worldUserListEntity.getUserInfoEntity().getSuid(), "TESTCODESUID");
////        }catch(Exception e)
////        {
////            System.out.println(e.getMessage());
////
////        }
//    }
//
//}
