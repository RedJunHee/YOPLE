package com.map.mutual.side.common.exception.handler;


import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * controller 전역적인 예외처리
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    // 사용자 정의 예외
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseJsonObject> handleRuntimeException(RuntimeException ex) {
        logger.debug("RuntimeExceptionHandler : " + ex.getMessage());
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.SYSTEM_ERROR), HttpStatus.BAD_REQUEST);
    }

    // 로그인시 존재하지 않는 유저인 경우 발생하는 Exception
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseJsonObject> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        logger.debug("UsernameNotFoundExceptionHandler : " + ex.getMessage());
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.USER_NOT_FOUND), HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {

            // "유효성 검사 실패 : " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            logger.debug("Parameter Exception : " + ex.getMessage());
            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.PARAMETER_CHECK_FAILED), HttpStatus.OK);
    }

    // 사용자 정의 예외
    @ExceptionHandler(YOPLEServiceException.class)
    public ResponseEntity<ResponseJsonObject> handleYOPLEServiceException(YOPLEServiceException ex) {
        logger.debug("YOPLEServiceExceptionHandler : " + ex.getMessage());
        // HttpStatus 200 정상적인 응답이지만 서비스 응답코드는 ex.getResponseJsonObject에 담김.
        return new ResponseEntity<>(ex.getResponseJsonObject(), HttpStatus.OK);
    }
}
