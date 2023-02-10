package com.mihalis.yandexbot;

import com.mihalis.yandexbot.telegram.Bot;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@SpringBootApplication
public class YandexFoodDeliveryBotApplication {
    @Autowired
    private Bot bot;

    public static void main(String[] args) {
        SpringApplication.run(YandexFoodDeliveryBotApplication.class, args);
    }

    @SneakyThrows
    @PostConstruct
    private void registerTelegramBot() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);

        log.info("Telegram bot successfully registered!");
    }
}
