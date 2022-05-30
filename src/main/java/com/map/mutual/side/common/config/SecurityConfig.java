package com.map.mutual.side.common.config;

import com.map.mutual.side.common.JwtTokenProvider;
import com.map.mutual.side.common.exception.handler.AuthenticationExceptionHandler;
import com.map.mutual.side.common.exception.handler.AuthorizationExceptionHandler;
import com.map.mutual.side.common.filter.AuthorizationCheckFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Class       : SecurityConfig
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-11] - 조 준희 - Class Create
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 3. provider
    private final JwtTokenProvider jwtTokenProvider;
    // 4. 401,403 Handler
    private final AuthenticationExceptionHandler authenticationExceptionHandler;
    private final AuthorizationExceptionHandler authorizationExceptionHandler;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public SecurityConfig( JwtTokenProvider jwtTokenProvider
            , AuthenticationExceptionHandler authenticationExceptionHandler
            , AuthorizationExceptionHandler authorizationExceptionHandler
            , AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationExceptionHandler = authenticationExceptionHandler;
        this.authorizationExceptionHandler = authorizationExceptionHandler;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    /**
     * 전반적인 Spring Security의 설정
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()
                .formLogin() .disable()
                //  예외 처리 지정
                .exceptionHandling()
                .authenticationEntryPoint(authenticationExceptionHandler)       //401 Error Handler
                .accessDeniedHandler(authorizationExceptionHandler)                //403 Error Handler

                // enable h2-console
                .and()
                .headers()


                .xssProtection()//xss 필터 추가
                .and()
                .contentSecurityPolicy("script-src 'self'")


                .and()
                .frameOptions()
                .sameOrigin()       // 동일 도메인에서는 iframe 접근 가능

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().configurationSource(corsConfigurationSource())
                /**
                 * URI별 인가 정보 셋팅.
                 */
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

                // auth
                .antMatchers(HttpMethod.POST,"/auth/sms-authentication-request/**").permitAll() // SMS 인증번호 요청하기.
                .antMatchers(HttpMethod.POST,"/auth/sms-authentication-response/**").permitAll() // SMS 인증번호 확인 요청하기.
                .antMatchers(HttpMethod.POST,"/auth/access-refresh/**").permitAll()  // 액세스 토큰 갱신하기.
                .antMatchers(HttpMethod.POST,"/auth/refresh-refresh/**").authenticated() // 리프레시 토큰 갱신.

                // user
                .antMatchers(HttpMethod.POST,"/user/signup/**").permitAll()                 // 사용자 회원가입
                .antMatchers(HttpMethod.POST,"/user/world/user/**").authenticated()         //월드에 참여하기
                .antMatchers(HttpMethod.GET,"/user/check-userid/**").permitAll()            // 유저 ID 중복체크
                .antMatchers(HttpMethod.GET,"/user/find-user/**").authenticated()           // 사용자 검색하기.
                .antMatchers(HttpMethod.GET,"/user/world/users/**").authenticated()         // 월드 참여자 조회
                .antMatchers(HttpMethod.GET,"/user/user/**").authenticated()                // 사용자 상세정보 조회
                .antMatchers(HttpMethod.PATCH,"/user/user/**").authenticated()              // 사용자 상세정보 수정
                .antMatchers(HttpMethod.DELETE,"/user/user/**").authenticated()             // 사용자 로그아웃
                .antMatchers(HttpMethod.POST,"/user/user/world/**").authenticated()         // 월드에 사용자 초대하기.
                .antMatchers(HttpMethod.GET,"/user/notification/**").authenticated()        // 사용자 알림 조회하기.
                .antMatchers(HttpMethod.GET,"/user/invite-response/**").authenticated()     // 월드 초대 응답하기.
                .antMatchers(HttpMethod.POST,"/user/report/**").authenticated()              // 사용자 신고하기.
                .antMatchers(HttpMethod.POST,"/user/block/**").authenticated()               // 사용자 차단하기.
                .antMatchers(HttpMethod.POST,"/user/review/report/**").authenticated()       // 리뷰 신고하기.
                .antMatchers(HttpMethod.DELETE,"/user/withdrawal").authenticated()            // 유저 탈퇴하기.
                .antMatchers(HttpMethod.GET,"/user/newNotiCheck/**").authenticated()       // 최신 알림 여부 확인.

                // world
                .antMatchers(HttpMethod.POST,"/world/world/**").authenticated() // 월드 생성하기.
                .antMatchers(HttpMethod.PATCH,"/world/world/**").authenticated() // 월드 수정하기
                .antMatchers(HttpMethod.GET,"/world/world/**").authenticated() // 월드 상세정보 조회
                .antMatchers(HttpMethod.GET,"/world/user/worlds/**").authenticated() // 참여 중인 월드 리스트 조회
                .antMatchers(HttpMethod.GET,"/world/user/auth-check/**").authenticated() // 월드 입장 권한 체크
                .antMatchers(HttpMethod.GET,"/world/review/worlds/**").authenticated() // 리뷰가 등록된 월드 리스트 조회
                .antMatchers(HttpMethod.GET,"/world/code-validation/**").permitAll() // 월드 초대 코드 유효성 체크.

                // review
                .antMatchers(HttpMethod.POST,"/review/review/**").authenticated() // 리뷰 작성하기.
                .antMatchers(HttpMethod.PUT,"/review/review/**").authenticated() // 리뷰 수정하기.
                .antMatchers(HttpMethod.DELETE,"/review/review/**").authenticated() // 리뷰 삭제하기.
                .antMatchers(HttpMethod.GET,"/review/review/**").authenticated() // 리뷰 조회하기.
                .antMatchers(HttpMethod.GET,"/review/myReviews/**").authenticated() // 내가 쓴 리뷰 조회하기.
                .antMatchers(HttpMethod.GET,"/review/worldPin/placeInRange/**").authenticated() // 지도 범위 핀 조회하기.
                .antMatchers(HttpMethod.GET,"/review/placeDetail/**").authenticated() // 장소에 등록된 리뷰 조회하기.

                .anyRequest().authenticated() // 그 외 나머지 리소스들은 무조건 인증을 완료해야 접근 가능
                .and()
                //AuthenticationFilterChain- UsernamePasswordAuthenticationFilter 전에 실행될 커스텀 필터 등록
                .addFilterBefore(new AuthorizationCheckFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        //.apply(new JwtSecurityConfig(jwtTokenProvider));


//        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL); //SecurityContext 새로운 쓰레드 생성 시 전파 설정
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // - (3)
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
