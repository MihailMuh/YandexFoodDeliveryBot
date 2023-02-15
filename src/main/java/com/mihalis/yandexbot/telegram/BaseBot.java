package com.mihalis.yandexbot.telegram;

import com.mihalis.yandexbot.callback.Callback;
import com.mihalis.yandexbot.command.Command;
import com.mihalis.yandexbot.handler.Handler;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class BaseBot extends TelegramLongPollingBot {
    private static final ArrayList<Command> commands = new ArrayList<>();

    private static final ArrayList<Callback> callbacks = new ArrayList<>();

    private static final ArrayList<Handler> handlers = new ArrayList<>();

    @Getter
    private final String botUsername;

    @Getter
    private final String botToken;

    public BaseBot(DefaultBotOptions botOptions, String botUsername, String botToken, ConfigurableListableBeanFactory beanFactory) {
        super(botOptions);

        this.botUsername = botUsername;
        this.botToken = botToken;

        registerCommands(beanFactory);
        registerCallbacks(beanFactory);
        registerHandlers(beanFactory);
    }

    // this just a methods that wrap into @SneakyThrows
    @Override
    @SneakyThrows
    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> executeAsync(Method method) {
        return super.executeAsync(method);
    }

    @Override
    @SneakyThrows
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        return super.execute(method);
    }

    @Override
    @SneakyThrows
    public CompletableFuture<Message> executeAsync(SendPhoto sendPhoto) {
        return super.executeAsync(sendPhoto);
    }

    protected void processCommandUpdate(Update update) {
        Message message = update.getMessage();

        for (Command command : commands) {
            if (message.getText().equals(command.getCommandIdentifier())) {
                command.processMessage(this, message);
                return;
            }
        }
    }

    protected void processNonCommandUpdate(Update update) {
        Parcel parcel = createParcel(update);

        if (update.hasCallbackQuery()) {
            for (Callback callback : callbacks) {
                if (callback.relevantCondition(update.getCallbackQuery())) {
                    callback.handleParcel(parcel);
                    return;
                }
            }
        }

        if (update.hasMessage()) {
            for (Handler handler : handlers) {
                if (handler.relevantCondition(update.getMessage())) {
                    handler.handleParcel(parcel);
                    return;
                }
            }
        }
    }

    private Parcel createParcel(Update update) {
        Message message = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage() : update.getMessage();
        return new Parcel(this, message);
    }

    private void registerCommands(ConfigurableListableBeanFactory beanFactory) {
        for (String name : beanFactory.getBeanNamesForType(Command.class)) {
            commands.add(beanFactory.getBean(name, Command.class));
        }
    }

    private void registerCallbacks(ConfigurableListableBeanFactory beanFactory) {
        for (String name : beanFactory.getBeanNamesForType(Callback.class)) {
            callbacks.add(beanFactory.getBean(name, Callback.class));
        }
    }

    private void registerHandlers(ConfigurableListableBeanFactory beanFactory) {
        for (String name : beanFactory.getBeanNamesForType(Handler.class)) {
            handlers.add(beanFactory.getBean(name, Handler.class));
        }
    }
}
