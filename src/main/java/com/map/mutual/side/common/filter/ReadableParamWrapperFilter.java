package com.map.mutual.side.common.filter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.*;
/**
 * Class       : ReadableParamWrapperFilter
 * Author      : 조 준 희
 * Description : HttpServletWrapper로 요청 정보를 변경 + 필터로 래퍼클래스를 프로세스하게 만듬.
 * History     : [2022-03-16] - 조 준희 - Class Create
 */
@WebFilter(urlPatterns = "/*")
public class ReadableParamWrapperFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        ReadableParamWrapper wrapper = new ReadableParamWrapper((HttpServletRequest) servletRequest);
        filterChain.doFilter(wrapper, servletResponse);
    }

    @Override
    public void destroy() {
        // Do nothing
    }

    public class ReadableParamWrapper extends HttpServletRequestWrapper {
        private final Logger logger = LogManager.getLogger(ReadableParamWrapper.class);
        private final Charset encoding;
        private byte[] rawData; // body
        private Map<String, String[]> params = new HashMap<>();


        public ReadableParamWrapper(HttpServletRequest request)  {

            super(request);
            this.params.putAll(request.getParameterMap()); // 원래의 파라미터 저장

            String charEncoding = request.getCharacterEncoding();
            this.encoding = StringUtils.isBlank(charEncoding)? StandardCharsets.UTF_8 : Charset.forName(charEncoding);

            try{
                InputStream is = request.getInputStream();
                this.rawData = IOUtils.toByteArray(is); //

                String collect = this.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

                //body가 비었을 경우.
                if(StringUtils.isEmpty(collect)){
                    return ;
                }

                //파일 업로드인 ContentType이 MULTIPART FORM일 경우 로깅 제외.
                if(request.getContentType() != null && request.getContentType()
                        .contains(ContentType.MULTIPART_FORM_DATA.getMimeType())){
                    return;
                }

                JSONParser jsonParser = new JSONParser();
                Object parser = jsonParser.parse(collect);

                if(parser instanceof JSONArray){
                    JSONArray jsonArray = (JSONArray) jsonParser.parse(collect);
                    setParameter("requestArrayParam",jsonArray.toJSONString());
                }
                else{
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(collect);
                    Iterator iterator = jsonObject.keySet().iterator();
                    while(iterator.hasNext()){
                        String key = (String) iterator.next();
                        String value= jsonObject.get(key).toString().replace("\"","\\\"");
                        setParameter(key,value);
                    }
                }
            }catch(Exception e)
            {
                logger.error("ReadableParamWrapper init error");
            }

        }


        /**
         * request 에 담긴 정보를 JSONObject 형태로 반환한다.
         * @param request
         * @return
         */
        private  Map<String,String> getParams(HttpServletRequest request) {
            Map<String,String> map = new HashMap<>();
            Enumeration<String> params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String param = params.nextElement();
                String replaceParam = param.replaceAll("\\.", "-");
                map.put(replaceParam, request.getParameter(param));
            }
            return map;
        }
        @Override
        public String getParameter(String name) {
            String[] paramArray = getParameterValues(name);
            if (paramArray != null && paramArray.length > 0) {
                return paramArray[0];
            } else {
                return null;
            }
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.unmodifiableMap(params);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(params.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] result = null;
            String[] dummyParamValue = params.get(name);

            if (dummyParamValue != null) {
                result = new String[dummyParamValue.length];
                System.arraycopy(dummyParamValue, 0, result, 0, dummyParamValue.length);
            }
            return result;
        }

        public void setParameter(String name, String value) {
            String[] param = {value};
            setParameter(name, param);
        }

        public void setParameter(String name, String[] values) {
            params.put(name, values);
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.rawData);

            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    // Do nothing
                }

                public int read() {
                    return byteArrayInputStream.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(this.getInputStream(), this.encoding));
        }


    }
}
