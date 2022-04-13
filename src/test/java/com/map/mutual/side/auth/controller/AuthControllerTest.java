package com.map.mutual.side.auth.controller;

import com.map.mutual.side.AbstractControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractControllerTest {

    @Autowired
    public AuthController authController;

    @Override
    protected Object object() {
        return authController;
    }


    @Test
    @DisplayName("sms인증번호요청_폰번호_길이초과")
    @Transactional
    public void sms인증번호요청_폰번호_길이초과() throws Exception {

        String body = "{\"phone\":\"010123412834\",\"duid\":\"test_duid\" }";

            mockMvc.perform(post("/auth/sms-authentication-request")
                            .content(body).contentType(this.contentType))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.code",is(400)));

    }

    @Test
    @DisplayName("sms인증번호요청_폰번호_길이부족")
    @Transactional
    public void sms인증번호요청_폰번호_길이부족() throws Exception {

        String body = "{\"phone\":\"010123834\",\"duid\":\"test_duid\" }";

        mockMvc.perform(post("/auth/sms-authentication-request")
                        .content(body).contentType(this.contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code",is(400)));

    }

    @Test
    @DisplayName("sms인증번호요청_폰번호_문자포함")
    @Transactional
    public void sms인증번호요청_폰번호_문자포함() throws Exception {

        String body = "{\"phone\":\"010ㅁㅇㅉㄸ1234\",\"duid\":\"test_duid\" }";

        mockMvc.perform(post("/auth/sms-authentication-request")
                        .content(body).contentType(this.contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code",is(400)));

    }

    @Test
    @DisplayName("sms인증번호요청_폰번호_010시작안함")
    @Transactional
    public void sms인증번호요청_폰번호_010시작안함() throws Exception {

        String body = "{\"phone\":\"01312341283\",\"duid\":\"test_duid\" }";

        mockMvc.perform(post("/auth/sms-authentication-request")
                        .content(body).contentType(this.contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code",is(400)));

    }

    @Test
    @DisplayName("sms인증번호요청_정상")
    @Transactional
    public void sms인증번호요청_정상() throws Exception {

        String body = "{\"phone\":\"01027090787\",\"duid\":\"test_duid\" }";

        mockMvc.perform(post("/auth/sms-authentication-request")
                        .content(body).contentType(this.contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code",is(200)));

    }




}
