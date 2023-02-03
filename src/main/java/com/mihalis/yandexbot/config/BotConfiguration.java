package com.mihalis.yandexbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
class BotConfiguration {
    @Bean
    public DefaultBotOptions getBotConfiguration(@Value("${app.parallelism}") int parallelism) {
        DefaultBotOptions options = new DefaultBotOptions();
        options.setMaxThreads(parallelism);

        return options;
    }
}
