package com.example.async.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Bean
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean
    public Executor executorService() {
        return Executors.newFixedThreadPool(10);
    }
}
