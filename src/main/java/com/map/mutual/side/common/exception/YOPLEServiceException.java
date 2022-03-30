package com.map.mutual.side.common.exception;


import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;


/**
 * Class       : YOPLEServiceException
 * Author      : 조 준 희
 * Description : YOPLE 서비스 간에 명시된 API응답 코드를 활용한 Exception관리 객체
 * ExceptionHandler = com.map.mutual.side.common.exception.handler.GlobalExceptionHandler.java
 * History     : [2022-03-13] - 조 준희 - Class Create
 */
public class YOPLEServiceException extends RuntimeException{
    private final ApiStatusCode errorStatusCode ;
    private final ResponseJsonObject responseJsonObject;

    public ResponseJsonObject getResponseJsonObject(){
        return responseJsonObject;
    }

    public YOPLEServiceException(ApiStatusCode apiStatusCode) {
        this.errorStatusCode = apiStatusCode;
        responseJsonObject = ResponseJsonObject.withStatusCode(errorStatusCode);
    }
    public YOPLEServiceException(ApiStatusCode apiStatusCode, String msg) {
        super(msg);
        this.errorStatusCode = apiStatusCode;
        responseJsonObject = ResponseJsonObject.withStatusCode(errorStatusCode);
        responseJsonObject.getMeta().setMsg(msg);
    }


}
