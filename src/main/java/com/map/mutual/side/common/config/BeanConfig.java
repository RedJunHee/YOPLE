package com.map.mutual.side.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartResolver;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class       : BeanConfig
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-03-11] - 조 준희 - Class Create
 */
@Configuration
public class BeanConfig {
    @Bean  // 어떤 암호화방식 사용할 것인지 빈 등록
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 기본은  BCryptPasswordEncoder 방식
    }

    @Bean
    public ObjectMapper objectMapper()
    {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);   // Java 객체를 JSON으로 Serialize할 때 null값은 제외
        objectMapper.registerModule(simpleModule);
        return  objectMapper;
    }

    /*
    entity <-> dto 변환하기 위한 빈
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /*
    JPA Query, DSL 을 위한 빈
     */
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }


    /*
    파일 처리를 위한 빈
     */
    private final int FILE_MAX_UPLOAD_SIZE = 10485760; // 1024 * 1024 * 10

//    @Bean
//    public MultipartResolver multipartResolver() {
//        org.springframework.web.multipart.commons.CommonsMultipartResolver multipartResolver = new org.springframework.web.multipart.commons.CommonsMultipartResolver();
//        multipartResolver.setMaxUploadSize(FILE_MAX_UPLOAD_SIZE);
//        return multipartResolver;
//    }


}
