package com.map.mutual.side.common.filter;

import com.map.mutual.side.common.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class       : AuthenticationCheckFilter
 * Author      : 조 준 희
 * Description : 요청의 header 내에 jwt 토큰이 Bearer 토큰으로 들어있는지 체크
 * History     : [2022-03-11] - 조 준희 - Class Create
 */
public class AuthorizationCheckFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationCheckFilter.class);

    public static final String ACCESS_TOKEN="ACCESS_TOKEN";
    public static final String REFRESH_TOKEN="REFRESH_TOKEN";

    private final JwtTokenProvider tokenProvider ;

    public AuthorizationCheckFilter(JwtTokenProvider provider) {
        this.tokenProvider = provider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws BadCredentialsException,ServletException, IOException {
        String jwt = resolveToken(request);

        // token이 유효한지 확인
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAccessAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);       // token에 authentication 정보 삽입
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(ACCESS_TOKEN);                            // Authorization 헤더 꺼냄
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {     // JWT 토큰이 존재하는지 확인
            return bearerToken.substring(7);           // "Bearer"를 제거한 accessToken 반환
        }
        return null;
    }
}
