package com.map.mutual.side.auth.repository.dsl.impl;

import com.map.mutual.side.auth.repository.UserWorldInvitingLogRepo;
import com.map.mutual.side.auth.repository.dsl.UserWorldInvitingLogRepoDSL;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class       : UserWorldInvitingLogRepoDSLImplTest
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-13] - 조 준희 - Class Create
 */

@DataJpaTest
@ActiveProfiles("test")
class UserWorldInvitingLogRepoDSLImplTest {

    @MockBean
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private UserWorldInvitingLogRepo dsl;

    @Test
    public void UserWorldInvitingLogRepoDSLImpl(){
        dsl.InvitedNotiList("YO");

    }

}