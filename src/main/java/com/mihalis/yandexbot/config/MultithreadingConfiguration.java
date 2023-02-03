package com.mihalis.yandexbot.config;

import com.mihalis.yandexbot.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.max;

@Configuration
public class MultithreadingConfiguration {
    @Bean(name = "executorService", destroyMethod = "shutdown")
    public ExecutorService getExecutorService(@Value("${app.parallelism}") int parallelism) {
        return Executors.newWorkStealingPool(parallelism);
    }

    @Bean(name = "pagePoolCapacity")
    public int getPagePoolCapacity(@Autowired AddressRepository addressRepository,
                                   @Value("${app.parallelism}") int parallelism) {
        return max(addressRepository.items().size() * 2, parallelism);
    }
}
