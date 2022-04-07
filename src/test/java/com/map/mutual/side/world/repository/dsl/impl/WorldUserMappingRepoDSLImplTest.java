package com.map.mutual.side.world.repository.dsl.impl;

import com.map.mutual.side.auth.model.dto.UserInWorld;
import com.querydsl.core.Tuple;
import javafx.util.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.lambda.Seq.seq;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Class       : WorldUserMappingRepoDSLImplTest
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-04-07] - 조 준희 - Class Create
 */
class WorldUserMappingRepoDSLImplTest {


    @Test
    @DisplayName("List Table Join")
    public void joinTest()
    {

        List<UserInWorld> userInfoInWorld = new ArrayList<>();
        List<Pair> reviewCnt = new ArrayList<>();

        userInfoInWorld.add(UserInWorld.builder().suid("1").build());
        userInfoInWorld.add(UserInWorld.builder().suid("2").build());
        userInfoInWorld.add(UserInWorld.builder().suid("3").build());
        userInfoInWorld.add(UserInWorld.builder().suid("4").build());
        userInfoInWorld.add(UserInWorld.builder().suid("5").build());

        reviewCnt.add(new Pair("1",4));
        reviewCnt.add(new Pair("2",3));
        reviewCnt.add(new Pair("3",55));
        reviewCnt.add(new Pair("4",32));
        reviewCnt.add(new Pair("5",5));

        List<Object> list = seq(userInfoInWorld)
                .flatMap( user -> seq(reviewCnt)
                        .filter(review -> user.getSuid().equals(review.getKey()))
                        .onEmpty(null)
                        .map(review -> tuple(user,review)))
                .sorted( Comparator.comparing(v -> Long.parseLong(v.v2.getValue().toString()))).reverse()
                .map(v -> v.v1)
                .collect(Collectors.toList());


        System.out.println(list);
    }

}