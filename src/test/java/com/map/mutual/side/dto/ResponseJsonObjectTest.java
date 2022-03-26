package com.map.mutual.side.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Class       : ResponseJsonObjectTest
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-11] - 조 준희 - Class Create
 */

class ResponseJsonObjectTest {

    ObjectMapper om ;

    @Getter
    class MutualMap
    {
        private String id;
        private String gender;
        private Long age;

        public MutualMap(String id, String gender, Long age)
        {
            this.id = id;
            this.gender = gender;
            this.age = age;
        }

    }

    @BeforeEach
    public void beforeEach()
    {
        om = new ObjectMapper();
    }


    @Test
    @DisplayName("성공상태 메시지 확인")
    public void successStatusTest()
    {

        ResponseJsonObject response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

        try {
            System.out.println( om.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("성공상태 메시지 확인 - Null Field Ignore")
    public void successStatusTest2()
    {
        ResponseJsonObject response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

        om.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        try {
            System.out.println( om.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("성공상태 메시지 확인 + data포함")
    public void successStatusTest3()
    {

        ResponseJsonObject response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

        MutualMap mutualMap = new MutualMap("redjoon10@gmail.com", "redjoon10", 29l);

        response.setData(mutualMap);
        ResponseEntity<ResponseJsonObject> responseEntity = new ResponseEntity(response, HttpStatus.OK);
        try {
            System.out.println( om.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    @Test
    @DisplayName("성공상태 메시지 확인 + data포함 => ResponseEntity로 변환")
    public void successStatusTest7()
    {

        ResponseJsonObject response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

        MutualMap mutualMap = new MutualMap("redjoon10@gmail.com", "redjoon10", 29l);

        response.setData(mutualMap);
        ResponseEntity<ResponseJsonObject> responseEntity = new ResponseEntity(response, HttpStatus.OK);
        try {
            System.out.println( om.writeValueAsString(responseEntity));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    @Test
    @DisplayName("성공상태 메시지 확인 + data 포함 - Null Field Ignore")
    public void successStatusTest4()
    {
        ResponseJsonObject response =  ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

        MutualMap mutualMap = new MutualMap("redjoon10@gmail.com", "redjoon10", null);

        response.setData(mutualMap);
        om.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        try {
            System.out.println( om.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("실패상태 메시지 확인")
    public void failedStatusTest()
    {

        ResponseJsonObject response =  ResponseJsonObject.withStatusCode(ApiStatusCode.FORBIDDEN);

        try {
            System.out.println( om.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("실패상태 메시지 확인 - Null Field Ignore")
    public void failedStatusTest2()
    {
        ResponseJsonObject response =  ResponseJsonObject.withStatusCode(ApiStatusCode.FORBIDDEN);

        om.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        try {
            System.out.println( om.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("실패상태 메시지 확인 + data포함")
    public void failedStatusTest3()
    {

        ResponseJsonObject response =  ResponseJsonObject.withStatusCode(ApiStatusCode.FORBIDDEN);

        MutualMap mutualMap = new MutualMap("redjoon10@gmail.com", "redjoon10", 29l);

        response.setData(mutualMap);
        try {
            System.out.println( om.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("실패상태 메시지 확인 + data 포함 - Null Field Ignore")
    public void failedStatusTest4()
    {
        ResponseJsonObject response =  ResponseJsonObject.withStatusCode(ApiStatusCode.FORBIDDEN);

        MutualMap mutualMap = new MutualMap("redjoon10@gmail.com", "redjoon10", null);

        response.setData(mutualMap);
        om.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        try {
            System.out.println( om.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }




}