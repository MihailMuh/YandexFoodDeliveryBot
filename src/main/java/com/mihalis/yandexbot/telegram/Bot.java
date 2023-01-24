package com.mihalis.yandexbot.telegram;

import com.mihalis.yandexbot.commands.AddressCommand;
import com.mihalis.yandexbot.commands.StartCommand;
import com.mihalis.yandexbot.commands.StopCommand;
import com.mihalis.yandexbot.commands.SupportCommand;
import com.mihalis.yandexbot.handlers.CancelButtonHandler;
import com.mihalis.yandexbot.handlers.NewAddressHandler;
import com.mihalis.yandexbot.handlers.RandomTextHandler;
import com.mihalis.yandexbot.selenium.YandexFoodService;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@Component
public class Bot extends TelegramLongPollingCommandBot {
    private final CancelButtonHandler cancelButtonHandler;
    private final NewAddressHandler newAddressHandler;
    private final RandomTextHandler randomTextHandler;

    @Getter
    @Value("${spring.telegram.bot.username}")
    private String botUsername;

    @Getter
    @Value("${spring.telegram.bot.token}")
    private String botToken;

    public Bot(DefaultBotOptions defaultBotOptions, CancelButtonHandler cancelButtonHandler,
               NewAddressHandler newAddressHandler, StartCommand startCommand, AddressCommand addressCommand,
               SupportCommand supportCommand, StopCommand stopCommand, RandomTextHandler randomTextHandler,
               YandexFoodService yandexFoodService) {

        super(defaultBotOptions);
        this.cancelButtonHandler = cancelButtonHandler;
        this.newAddressHandler = newAddressHandler;
        this.randomTextHandler = randomTextHandler;

        registerAll(startCommand, addressCommand, supportCommand, stopCommand);
        registerUsersForNotification(yandexFoodService);
    }

    @Override
    @SneakyThrows
    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> executeAsync(Method method) {
        return super.executeAsync(method);
    }

    public void executeAsync(String text, Message message) {
        PostMessage postMessage = new PostMessage(message);
        postMessage.setText(text);

        executeAsync(postMessage);
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
            if (newAddressHandler.handleUpdate(this, update.getMessage())) {
                return;
            }

            randomTextHandler.handleUpdate(this, update.getMessage());
        }
    }

    private void registerUsersForNotification(YandexFoodService yandexFoodService) {
        yandexFoodService.runScheduledCheckOfCost(deliveryData -> {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(deliveryData.userId());
            sendMessage.setText(deliveryData.address() + "\n" + deliveryData.cost());

            executeAsync(sendMessage);
        });
    }
}
