package com.mihalis.yandexbot.utils;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MINUTES;

@Component
public class Timer {
    private final ScheduledExecutorService timer;

    private ScheduledFuture<?> future;

    protected Timer() {
        timer = Executors.newScheduledThreadPool(1);
    }

    public void start(Runnable runnable) {
        if (future != null) {
            future.cancel(true);
        }
        future = timer.scheduleWithFixedDelay(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 15, 15, MINUTES);
    }

    @PreDestroy
    private void onShutdown() {
        future.cancel(true);
        timer.shutdown();
    }
}
