package com.mihalis.yandexbot.beans;

import com.mihalis.yandexbot.telegram.PostMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Random;

import static java.lang.Math.abs;

@Configuration
public class Telegram {
    @Bean
    @Scope(scopeName = "prototype")
    public Message getMessage() {
        Chat chat = new Chat();
        chat.setId(abs(new Random().nextLong()));

        Message message = new Message();
        message.setChat(chat);

        return message;
    }

    @Bean
    @Scope(scopeName = "prototype")
    public CallbackQuery getCallback(@Autowired Message message) {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setMessage(message);

        return callbackQuery;
    }

    @Bean
    @Scope(scopeName = "prototype")
    public PostMessage getPostMessage(@Autowired Message message) {
        return new PostMessage(message);
    }
}
