package com.mihalis.yandexbot.commands;

import com.mihalis.yandexbot.telegram.Bot;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StartCommand extends BaseCommand {
    private static final String greeting = "Привет!\n" +
            "Это бот, который уведомляет о текущей стоимости доставки " +
            "от магазина Яндекс Лавка (https://lavka.yandex.ru/54) " +
            "в сервисе Яндекс Еда (https://eda.yandex.ru/)\n" +
            "(т.к. только в Яндекс Лавке может быть бесплатная доставка, " +
            "в остальных есть сбор сервиса (около 50р))\n\n" +

            "Список доступных команд:\n" +
            "/start - показать это приветственное сообщение\n" +
            "/address - задать адрес доставки и получить текущую её стоимость\n" +
            "/stop - отменить автоматическое оповещение о текущей цене доставки (каждые 15 минут)\n" +
            "/support - поддержать автора :)";

    public StartCommand() {
        super("start");
    }

    @Override
    @SneakyThrows
    public void answer(Bot bot, Message message) {
        bot.executeAsync(greeting, message);
    }
}
