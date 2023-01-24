package com.mihalis.yandexbot.commands;

import com.mihalis.yandexbot.cache.AddressCache;
import com.mihalis.yandexbot.selenium.YandexFoodService;
import com.mihalis.yandexbot.telegram.Bot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StopCommand extends BaseCommand {
    private final YandexFoodService yandexFoodService;

    private final AddressCache addressCache;

    public StopCommand(YandexFoodService yandexFoodService, AddressCache addressCache) {
        super("stop");
        this.yandexFoodService = yandexFoodService;
        this.addressCache = addressCache;
    }

    @Override
    public void answer(Bot bot, Message message) {
        yandexFoodService.deleteAddress(message.getChatId());
        addressCache.deleteAddress(message.getChatId());

        bot.executeAsync("Операция отменена", message);
    }
}
