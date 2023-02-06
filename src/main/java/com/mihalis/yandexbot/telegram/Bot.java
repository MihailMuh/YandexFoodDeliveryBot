package com.mihalis.yandexbot.telegram;

import com.mihalis.yandexbot.commands.AddressCommand;
import com.mihalis.yandexbot.commands.StartCommand;
import com.mihalis.yandexbot.commands.StopCommand;
import com.mihalis.yandexbot.commands.SupportCommand;
import com.mihalis.yandexbot.handlers.CancelButtonHandler;
import com.mihalis.yandexbot.handlers.ChooseDeliveryAddressHandler;
import com.mihalis.yandexbot.handlers.RandomTextHandler;
import com.mihalis.yandexbot.service.YandexFoodService;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@Component
@AutoConfigureAfter(YandexFoodService.class)
public class Bot extends TelegramLongPollingCommandBot {
    private final CancelButtonHandler cancelButtonHandler;
    private final ChooseDeliveryAddressHandler chooseDeliveryAddressHandler;
    private final RandomTextHandler randomTextHandler;

    @Getter
    @Value("${telegram.bot.username}")
    private String botUsername;

    @Getter
    @Value("${telegram.bot.token}")
    private String botToken;

    public Bot(DefaultBotOptions defaultBotOptions, CancelButtonHandler cancelButtonHandler,
               ChooseDeliveryAddressHandler chooseDeliveryAddressHandler, StartCommand startCommand, AddressCommand addressCommand,
               SupportCommand supportCommand, StopCommand stopCommand, RandomTextHandler randomTextHandler,
               YandexFoodService yandexFoodService) {

        super(defaultBotOptions);
        this.cancelButtonHandler = cancelButtonHandler;
        this.chooseDeliveryAddressHandler = chooseDeliveryAddressHandler;
        this.randomTextHandler = randomTextHandler;

        registerAll(startCommand, addressCommand, supportCommand, stopCommand);
        registerUsersForNotification(yandexFoodService);
    }

    // this just a method that wraps into @SneakyThrows
    @Override
    @SneakyThrows
    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> executeAsync(Method method) {
        return super.executeAsync(method);
    }

    public void executeAsync(String text, Message message, InlineKeyboardMarkup keyboardMarkup) {
        PostMessage postMessage = new PostMessage(message);
        postMessage.setText(text);

        if (keyboardMarkup != null) {
            postMessage.setReplyMarkup(keyboardMarkup);
        }

        executeAsync(postMessage);
    }

    public void executeAsync(String text, Message message) {
        executeAsync(text, message, null);
    }

    @SneakyThrows
    public void execute(String text, Message message) {
        PostMessage postMessage = new PostMessage(message);
        postMessage.setText(text);

        execute(postMessage);
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            cancelButtonHandler.handleUpdate(this, update.getCallbackQuery());
        }

        if (update.hasMessage()) {
            if (chooseDeliveryAddressHandler.handleUpdate(this, update.getMessage())) {
                return;
            }

            randomTextHandler.handleUpdate(this, update.getMessage());
        }
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
