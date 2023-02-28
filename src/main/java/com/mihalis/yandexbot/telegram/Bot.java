package com.mihalis.yandexbot.telegram;

import com.mihalis.yandexbot.cache.FiniteStateMachine;
import com.mihalis.yandexbot.service.YandexFoodService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Bot extends BaseBot {
    private final FiniteStateMachine finiteStateMachine;

    @Getter
    @Value("${telegram.bot.username}")
    private String botUsername;

    @Getter
    @Value("${telegram.bot.token}")
    private String botToken;

    public Bot(ConfigurableListableBeanFactory beanFactory,
               DefaultBotOptions defaultBotOptions,
               YandexFoodService yandexFoodService, FiniteStateMachine finiteStateMachine) {
        super(defaultBotOptions, beanFactory);
        this.finiteStateMachine = finiteStateMachine;

        registerUsersForNotification(yandexFoodService);
    }

    public void cancelUserSessions() {
        // delete because we cant save whole browser state. It can raise errors
        for (long userId : finiteStateMachine.keys()) {
            new Parcel(this, userId).answerAsync("Ваша сессия истекла.\nЭта операция отменена.\nПопробуйте ещё раз");
            finiteStateMachine.delete(userId);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().isCommand()
                && !finiteStateMachine.hasState(update.getMessage().getChatId())) {

            processCommandUpdate(update);
            return;
        }
        processNonCommandUpdate(update);
    }

    private void registerUsersForNotification(YandexFoodService yandexFoodService) {
        yandexFoodService.runScheduledCheckOfCost(deliveryData ->
                new Parcel(this, deliveryData.getUserId()).answerAsync(
                        deliveryData.getAddress() + "\n" + deliveryData.getDeliveryCost(),
                        "Markdown"
                ));
    }
}
