package com.map.mutual.side.common.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.mutual.side.common.exception.CustomAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class       : AuthenticationExceptionHandler
 * Author      : 조 준 희
 * Description : SpringSecurity 인증절차 실패시 Exception Handle
 * History     : [2022-03-12] - 조 준희 - Class Create
 */
@Component
public class AuthenticationExceptionHandler  implements AuthenticationEntryPoint {

    private ObjectMapper om ;

    @Autowired
    public AuthenticationExceptionHandler(ObjectMapper om) {
        this.om = om;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ServletOutputStream out = response.getOutputStream();
        om.writeValue(out,new CustomAuthenticationException().getResponseJsonObject());
        out.flush();
    }
}
