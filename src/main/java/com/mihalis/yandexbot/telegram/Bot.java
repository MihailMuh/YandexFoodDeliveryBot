package com.mihalis.yandexbot.telegram;

import com.mihalis.yandexbot.cache.AddressState;
import com.mihalis.yandexbot.service.YandexFoodService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Bot extends BaseBot {
    private final AddressState addressState;

    public Bot(@Value("${telegram.bot.username}") String botUsername,
               @Value("${telegram.bot.token}") String botToken,
               ConfigurableListableBeanFactory beanFactory,
               DefaultBotOptions defaultBotOptions,
               YandexFoodService yandexFoodService, AddressState addressState) {
        super(defaultBotOptions, botUsername, botToken, beanFactory);
        this.addressState = addressState;

        registerUsersForNotification(yandexFoodService);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().isCommand()
                && !addressState.isActive(update.getMessage().getChatId())) {
            processCommandUpdate(update);
            return;
        }
        processNonCommandUpdate(update);
    }

    private void registerUsersForNotification(YandexFoodService yandexFoodService) {
        yandexFoodService.runScheduledCheckOfCost(deliveryData -> {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(deliveryData.getUserId());
            sendMessage.setText(deliveryData.getAddress() + "\n" + deliveryData.getDeliveryCost());

            executeAsync(sendMessage);
        });
    }
}
