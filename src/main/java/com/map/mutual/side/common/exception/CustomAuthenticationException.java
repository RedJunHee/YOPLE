package com.map.mutual.side.common.exception;

import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;

/**
 * Class       : CustomAuthenticationException
 * Author      : 조 준 희
 * Description : Spring Security JWT 인증 과정에서 생기는 ExceptionHandler에서 사용되어지는 객체.
 * ExceptionHandler = com.review.storereview.common.exception.handler.AuthenticationExceptionHandler.java
 * History     : [2022-01-10] - 조 준희 - Class Create
 */
public class CustomAuthenticationException   {
    private final ApiStatusCode errorStatusCode = ApiStatusCode.UNAUTHORIZED;
    private final ResponseJsonObject responseJsonObject;

    public ResponseJsonObject getResponseJsonObject(){
        return responseJsonObject;
    }
    

    public CustomAuthenticationException() {
        responseJsonObject = ResponseJsonObject.withStatusCode(errorStatusCode);
    }



}
