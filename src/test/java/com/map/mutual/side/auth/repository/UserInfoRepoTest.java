//package com.map.mutual.side.auth.repository;
//
//import com.map.mutual.side.auth.model.entity.UserEntity;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class UserInfoRepoTest {
//
//    @Autowired
//    UserInfoRepo userInfoRepo;
//
//    @DisplayName("유저 저장")
//    public void 유저_저장(){
//
//        UserEntity user = UserEntity.builder().suid("TESTSUID")
//                .userId("redjooin10")
//                .name("name")
//                .phone("010-2709-0787")
//                .build();
//
//        userInfoRepo.save(user);
//
//    }
//    @Test
//    public void test(){}
//
//
//}
