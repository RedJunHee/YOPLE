package com.map.mutual.side.common.filter;

import com.map.mutual.side.common.utils.YOPLEUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Class       : HttpServletFilter
 * Author      : 조 준 희
 * Description : HttpServletWrapper로 요청 정보를 변경 + 필터로 래퍼클래스를 프로세스하게 만듬.
 * History     : [2022-03-16] - 조 준희 - Class Create
 * History     : [2022-06-02] - 김재중 - Class 명 HttpServeletFilter 변경
 *                            - Request, Response 분리 / XSS 처리
 */
@Component
@Order(value = 101)
public class HttpServletFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpRequestReadableParamWrapper wrapper = new HttpRequestReadableParamWrapper((HttpServletRequest) servletRequest);
        HttpResponseWrapper responseWrapper = new HttpResponseWrapper((HttpServletResponse) servletResponse);

        filterChain.doFilter(wrapper, responseWrapper);

        //Response 처리
        String responseMessage = responseWrapper.getDataStreamToString();
        responseMessage = YOPLEUtils.DeClearXSS(responseMessage);
        byte[] responseMessageBytes = responseMessage.getBytes(StandardCharsets.UTF_8);
        int contentLength = responseMessageBytes.length;

        servletResponse.setContentLength(contentLength);
        servletResponse.getOutputStream().write(responseMessageBytes);
        servletResponse.flushBuffer();

    }

    @Override
    public void destroy() {
        // Do nothing
    }

}
