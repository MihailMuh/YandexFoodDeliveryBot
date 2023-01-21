package com.mihalis.yandexbot.telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class PostMessage extends SendMessage {
    public PostMessage(Message message) {
        setChatId(message.getChatId());
    }
}
