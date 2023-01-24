package com.mihalis.yandexbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MultithreadingConfiguration {
    @Bean(name = "executorService", destroyMethod = "shutdown")
    public ExecutorService getExecutorService() {
        return Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() * 2);
    }
}
