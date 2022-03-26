package com.map.mutual.side.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.mutual.side.auth.model.dto.SMSAuthReqeustDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;


@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
@Log4j2
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;



    @Test
    @Transactional
    @DisplayName("가입요청 Validation 테스트")
    void smsAuthenticationRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SMSAuthReqeustDto smsAuthReqeustDto = new SMSAuthReqeustDto(
                "111",
                "abcadsf",
                "1515151"
        );
        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/sms-authentication-request")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(smsAuthReqeustDto)));
//                .andExpect(result -> log.info("Result : {}", result));
    }
}
