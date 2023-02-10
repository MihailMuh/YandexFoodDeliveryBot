package com.mihalis.yandexbot.data;

public class StringMessages {
    public static final String greeting = "Привет!\n" +
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

    public static final String supportMe = "Если ты хочешь не только морально " +
            "поддержать разработчика: @mihalisM, то можешь скинуть по " +
            "номеру `+79087016312` какую-нибудь сумму 🙃";

    public static final String fasepalm = "🤦";

    public static final String love = "❤️";

    public static final String enterNewDeliveryAddress =
            "Вводи новый адрес в формате:\n" +
                    "Город, улица, дом\n\n" +
                    "Например:\n" +
                    "Екатеринбург, улица Малышева, 53А";

    public final static String chooseDeliveryAddress = "Вот, что я нашел. Выбери из них свой.\n" +
            "Если подходящего нет, нажми ПОВТОРИТЬ";


}
