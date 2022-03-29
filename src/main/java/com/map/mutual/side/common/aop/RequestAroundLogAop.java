package com.map.mutual.side.common.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.common.entity.ApiLog;
import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.svc.LogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** Class       : RqeustAroundLogAop (AOP)
 *  Author      : 조 준 희
 *  Description : com.review.storereview.controller.* 패키지 내 *Contorller.class 모든 클래스의
 *  메소드 모두 가로챔 => Request 정보 및 처리 결과를 LogService를 통해 Database에 저장
 *  History     : [2022-01-03] - 조 준희 - ApiLog Service, Repository 연동 후 정상적으로 Log Insert 개발 완료 ** 추후 Exception Return에 대해서 테스트 및 개발 필요.
 */

@Aspect
@Component
public class RequestAroundLogAop {
    private final ObjectMapper om ;
    private final LogService logService;

    @Autowired
    public RequestAroundLogAop(LogService logService, ObjectMapper om) {
        this.om = om;
        this.logService = logService;
    }

    //  execution(* com.map.mutual.side.*.controller 하위 패키지 내에
    //   *Controller 클래스의 모든 메서드 Around => Pointcut 설정
    @Around(value = "execution(* com.map.mutual.side.*.controller..*Controller.*(..))")
    public Object ApiLog(ProceedingJoinPoint joinPoint) throws Throwable { // 파라미터 : 프록시 대상 객체의 메서드를 호출할 때 사용

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String  inputParam = getWrapperParamJson(request.getParameterMap());
        String  outputMessage = "" ;
        char apiStatus = 'Y';
        String methodName = "";
        // Api log 담는 객체에 필요한 요소
        // ID(identity) / DATE(datetime) / SUID(varchar_12) / SAID(varchar_12) / API_NAME(varchar_20) / API_STATUS(char_1) / API_DESC(varchar_100) / PROCESS_TIME (Double)
        LocalDateTime date = LocalDateTime.now();
        String suid = "";
        long elapsedTime = 0L;
        StringBuilder apiResultDescription = new StringBuilder();
        // joinPoint 리턴 객체 담을 변수
        Object retValue = null;
        StopWatch stopWatch = new StopWatch();

        try {
            // API 요청 사용자 정보 가져오기.
            Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
            if(authenticationToken.getPrincipal().equals("anonymousUser") == false){
                //인증 객체에 저장되어있는 유저정보 가져오기.
                UserInfoDto userDetails = (UserInfoDto) authenticationToken.getPrincipal();
                suid = userDetails.getSuid();
            }

            methodName  = joinPoint.getSignature().getName();   // 메소드 이름 => Api명

            // 서비스 처리 시간 기록 시작
            stopWatch.start();
            retValue = joinPoint.proceed();   // 실제 대상 객체의 메서드 호출

            outputMessage = om.writeValueAsString( ((ResponseEntity)retValue).getBody());

        }
        catch(YOPLEServiceException ex){
            apiStatus = 'N';
            outputMessage = om.writeValueAsString(ex.getResponseJsonObject());
            throw ex;
        }
        catch(Exception ex) {
            apiStatus='N';
            //Exception
            ResponseJsonObject resDto = ResponseJsonObject.withStatusCode(ApiStatusCode.SYSTEM_ERROR);
            outputMessage = om.writeValueAsString(resDto);
            throw ex;
        }
        finally {
            // 서비스 처리 시간 기록 종료
            stopWatch.stop();

            //api 처리 정보 => INPUT + OUTPUT   ** Exception이 떨어졌을때 Exception정보도 담는지 확인 필요 함.
            apiResultDescription.append("\n[INPUT]").append(System.lineSeparator())
                    .append(inputParam).append(System.lineSeparator())
                    .append("[OUTPUT]").append(System.lineSeparator())
                    .append(outputMessage).append(System.lineSeparator());

            elapsedTime = stopWatch.getTotalTimeMillis();
            String apiDesc = "";
            if(apiResultDescription.length() > 8000)
                apiDesc = apiResultDescription.toString().substring(0,8000);
            else
                apiDesc = apiResultDescription.toString();

            //API_LOG담을 객체 생성 ( "SUID",  2022-01-14T12:55:22, "save", 'Y', [INPUT] [메서드 input] [OUTPUT] [메서드 output] , 3.0
            ApiLog data = ApiLog.builder()
                            .apiName(methodName)
                    .apiDesc(apiDesc)
                    .apiStatus(apiStatus)
                    .processTime((float) (elapsedTime*0.001))
                    .build();
            // INSERT
            logService.InsertApiLog(data);
        }

        return retValue;
    }
    private String getWrapperParamJson(Map<String,String[]> wrapperParams) throws JsonProcessingException {
        Map<String,String> params = new HashMap<>();

        for(String key : wrapperParams.keySet()){
            String[] values = wrapperParams.get(key);
            String sumValue = "";
            for(String value : values)
                if(sumValue.equals("") == false)
                    sumValue+=", "+value;
                else
                    sumValue += value;
                params.put(key,sumValue);
        }
        return om.writeValueAsString(params);
    }

}
