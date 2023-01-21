package com.mihalis.yandexbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
class BotConfiguration {
    @Bean
    public DefaultBotOptions getBotConfiguration() {
        DefaultBotOptions options = new DefaultBotOptions();
        options.setMaxThreads(3);

        return options;
    }
}
