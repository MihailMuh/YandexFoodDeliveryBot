package com.mihalis.yandexbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MultithreadingConfiguration {
    @Value("${app.parallelism}")
    private int parallelism;

    @Bean(name = "executorService", destroyMethod = "shutdown")
    public ExecutorService getExecutorService() {
        return Executors.newWorkStealingPool(parallelism);
    }

    @Bean(name = "pagePoolCapacity")
    public int getPagePoolCapacity() {
        return parallelism;
    }
}
