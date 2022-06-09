package com.map.mutual.side;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@ServletComponentScan(basePackages = "com.map.mutual.side")
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaAuditing
@EnableAsync
public class MutualMapApplication {
    public static void main(String[] args) {

        // log4j2 ThreadContext Map Child Thread Inheritable Setting
        System.setProperty("isThreadContextMapInheritable","true");

        SpringApplication.run(MutualMapApplication.class, args);
    }
}
