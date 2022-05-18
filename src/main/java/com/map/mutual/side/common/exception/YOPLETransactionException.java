package com.map.mutual.side.common.exception;

import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;

public class YOPLETransactionException extends RuntimeException {
    private final ApiStatusCode errorStatusCode ;

    private final ResponseJsonObject responseJsonObject;

    public ResponseJsonObject getResponseJsonObject(){
        return responseJsonObject;
    }

    public YOPLETransactionException(ApiStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
        responseJsonObject = ResponseJsonObject.withStatusCode(errorStatusCode);
    }
}
