package com.mihalis.yandexbot.data;

public class StringMessages {
    public static final String greeting = """
            Привет!
            Это бот, который уведомляет о текущей стоимости доставки от магазина
            Яндекс Лавка (https://lavka.yandex.ru/54)
            в сервисе Яндекс Еда (https://eda.yandex.ru)
            (т.к. только в Яндекс Лавке может быть бесплатная доставка, в остальных есть сбор сервиса (около 50р))

            Список доступных команд:
            /start - показать это приветственное сообщение
            /address - задать адрес доставки и получить текущую её стоимость
            /stop - отменить автоматическое оповещение о текущей цене доставки (каждые 15 минут)
            /support - поддержать автора :)
            """;

    public static final String supportMe = """
            Если ты хочешь не только морально поддержать разработчика: @mihalisM,
            то можешь скинуть по номеру `+79087016312` какую-нибудь сумму 🙃
            """;

    public static final String fasepalm = "🤦";

    public static final String love = "❤️";

    public static final String enterNewDeliveryAddress = """
            Вводи новый адрес в формате:
                Город, улица, дом
                       
            Например:
                Екатеринбург, улица Малышева, 53А
            """;

    public final static String chooseDeliveryAddress = """
            Вот, что я нашел. Выбери из этих адресов свой.
            
            Если подходящего нет, нажми ПОВТОРИТЬ
            
            ОТМЕНА, чтобы отменить всё это (надо будет заново вызывать /address)
            """;
}
