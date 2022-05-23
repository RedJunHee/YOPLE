package com.map.mutual.side.common.exception;

import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;

public class YOPLETransactionException extends RuntimeException {
    private final ApiStatusCode errorStatusCode ;
    private final ResponseJsonObject responseJsonObject;

    public ResponseJsonObject getResponseJsonObject(){
        return responseJsonObject;
    }

    public YOPLETransactionException(ApiStatusCode apiStatusCode) {
        this.errorStatusCode = apiStatusCode;
        responseJsonObject = ResponseJsonObject.withStatusCode(errorStatusCode);
    }
    public YOPLETransactionException(ApiStatusCode apiStatusCode, String msg) {
        super(msg);
        this.errorStatusCode = apiStatusCode;
        responseJsonObject = ResponseJsonObject.withStatusCode(errorStatusCode);
        responseJsonObject.getMeta().setMsg(msg);
    }}
