package com.map.mutual.side.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
/**
 * fileName       : AsyncConfig
 * author         : kimjaejung
 * createDate     : 2022/03/31
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/31        kimjaejung       최초 생성
 *
 */
@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {
    @Bean(name = "YOPLE-Executor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(300);
        executor.setThreadNamePrefix("YOPLE-ASYNC-");
        executor.initialize();
        return executor;
    }
}
