package com.map.mutual.side;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ServletComponentScan(basePackages = "com.map.mutual.side")
@SpringBootApplication
@EnableAspectJAutoProxy
public class MutualMapApplication {
    public static void main(String[] args) {
        SpringApplication.run(MutualMapApplication.class, args);
    }
}
