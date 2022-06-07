package com.map.mutual.side.common.exception.handler;


import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.exception.YOPLETransactionException;
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

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * controller 전역적인 예외처리
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    // 사용자 정의 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseJsonObject> handleException(Exception ex) {
        logger.error("RuntimeExceptionHandler : {} \n StackTrace : " , ex.getMessage(), ex.getStackTrace());
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.SYSTEM_ERROR), HttpStatus.BAD_REQUEST);
    }

    // 로그인시 존재하지 않는 유저인 경우 발생하는 Exception
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseJsonObject> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        logger.debug("UsernameNotFoundExceptionHandler : {}" , ex.getMessage());
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.USER_NOT_FOUND), HttpStatus.OK);
    }

    // @RequestBody, @RequestHeader 유효성 실패.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseJsonObject> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.error("파라미터 유효성 체크 실패. : {}" , ex.getMessage());
        ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.PARAMETER_CHECK_FAILED);

        if(ex.getConstraintViolations().isEmpty() == false)
        {
            String exceptionMsg = ex.getConstraintViolations().stream()
                    .map(v1 -> v1.getMessage())
                    .collect(Collectors.joining(","));
            response.getMeta().setMsg(exceptionMsg);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // @RequestBody 유효성 실패.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {

            // "유효성 검사 실패 : " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        logger.error("파라미터 유효성 체크 실패. : {}" , ex.getMessage());
        ResponseJsonObject response = ResponseJsonObject.withStatusCode(ApiStatusCode.PARAMETER_CHECK_FAILED);

        if(ex.getBindingResult().hasErrors())
            response.getMeta().setMsg(ex.getBindingResult().getFieldError().getDefaultMessage());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 사용자 정의 예외
    @ExceptionHandler({YOPLEServiceException.class})
    public ResponseEntity<ResponseJsonObject> handleYOPLEServiceException(YOPLEServiceException ex) {
        logger.debug("YOPLEServiceExceptionHandler : {}" , ex.getResponseJsonObject().getMeta().toString());
        // HttpStatus 200 정상적인 응답이지만 서비스 응답코드는 ex.getResponseJsonObject에 담김.
        return new ResponseEntity<>(ex.getResponseJsonObject(), HttpStatus.OK);
    }
    @ExceptionHandler({YOPLETransactionException.class})
    public ResponseEntity<ResponseJsonObject> handleYOPLETranServiceException(YOPLETransactionException ex) {
        logger.error("YOPLEServiceExceptionHandler : {}" ,ex.getMessage());
        // HttpStatus 200 정상적인 응답이지만 서비스 응답코드는 ex.getResponseJsonObject에 담김.
        return new ResponseEntity<>(ex.getResponseJsonObject(), HttpStatus.OK);
    }
}
