package com.mihalis.yandexbot.command;

import com.mihalis.yandexbot.telegram.Parcel;
import org.springframework.stereotype.Component;

@Component
public class StartCommand extends Command {
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
    public void answer(Parcel parcel) {
        parcel.answerAsync(greeting);
    }
}
