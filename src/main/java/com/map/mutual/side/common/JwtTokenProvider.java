package com.map.mutual.side.common;

import com.map.mutual.side.auth.model.dto.UserInfoDto;
import com.map.mutual.side.common.utils.CryptUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.*;

/**
 * Class       : JwtTokenProvider
 * Author      : 조 준 희
 * Description : JWT 토큰 공급자 객체.
 * History     : [2022-03-11] - 조 준희 - Class Create
 */
@Component
public class JwtTokenProvider {

    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";   // 사용자 권한 체크 위함

    private final String secret;
    private final long accessTokenExpiryInSeconds;
    private final long refreshTokenExpiryInSeconds;
    private Key key;

    private PasswordEncoder passwordEncoder;
    private final CryptUtils cryptUtils;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}")String secret,
                            @Value("${jwt.access-token-validity-in-seconds}")long accessTokenExpiryInSeconds,
                            @Value("${jwt.refresh-token-validity-in-seconds}")long refreshTokenExpiryInSeconds,
                            PasswordEncoder passwordEncoder,
                            CryptUtils cryptUtils) {
        this.secret = secret;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
        this.accessTokenExpiryInSeconds = accessTokenExpiryInSeconds * 1000;
        this.refreshTokenExpiryInSeconds = refreshTokenExpiryInSeconds * 1000;
        this.passwordEncoder = passwordEncoder;
        this.cryptUtils = cryptUtils;
    }

    /** Authentication정보로 토큰 생성
     * @param authentication
     * @return AccessToken
     */
    public String createAccessTokenFromAuthentication(Authentication authentication) throws Exception
    {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenExpiryInSeconds);

        UserInfoDto userDetails = (UserInfoDto) authentication.getPrincipal();

        String jwt ="";
        try {
            jwt = Jwts.builder()
                    .setSubject(cryptUtils.AES_Encode(userDetails.getSuid()))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .setExpiration(validity)
                    .compact();
        }catch(Exception e) {
            logger.error("JWT Access Token 생성 Exception "+ e.toString());
            e.printStackTrace();
            throw e;
        }
        return jwt;
    }
    public String createRefreshTokenFromAuthentication(String suid) throws Exception
    {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenExpiryInSeconds);

        String jwt ="";
        try {
            jwt = Jwts.builder()
                    .setSubject(cryptUtils.AES_Encode(suid))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .setExpiration(validity)
                    .compact();
        }catch(Exception e) {
            logger.debug(" JWT Builder Error :  "+ e.getMessage());
            throw e;
        }
        return jwt;
    }



    /**
     * 토큰으로 Authentication 객체 생성
     * @param token
     * @return
     */
    public Authentication getAccessAuthentication(String token) throws BadCredentialsException {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String suid = null ;
        try {
                suid = cryptUtils.AES_Decode(claims.getSubject());

        } catch (Exception e) {
            logger.error("Token get Authentication Error "+ e.getMessage());
            throw new BadCredentialsException("");
        }

        UserInfoDto principal = UserInfoDto.builder().suid(suid).build();

        return new UsernamePasswordAuthenticationToken(principal, token,null);

    }
    /**
     * 토큰으로 Authentication 객체 생성
     * @param token
     * @return
     */
    public Authentication getRefreshAuthentication(String token) throws BadCredentialsException {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String suid = null ;
        try {
                suid = cryptUtils.AES_Decode(claims.getSubject());

        } catch (Exception e) {
            logger.error("Token get Authentication Error "+ e.getMessage());
            throw new BadCredentialsException("");
        }

        UserInfoDto principal = UserInfoDto.builder().suid(suid).build();

        return new UsernamePasswordAuthenticationToken(principal, token,null);

    }


    /**
     * Token 벨리테이션
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.debug("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.debug("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.debug("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.debug("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

}
