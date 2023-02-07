package com.mihalis.yandexbot.telegram;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;

@RequiredArgsConstructor
public class Parcel {
    private final AbsSender bot;

    private final Message message;

    public Parcel(BaseBot bot, long userId) {
        this(bot, getRawMessage(userId));
    }

    public long getUserId() {
        return message.getChatId();
    }

    public String getText() {
        return message.getText();
    }

    @SneakyThrows
    public void answerAsync(String text, Object... args) {
        AbstractMessage message = createMessage(text, args);
        if (message.hasPhoto()) {
            bot.executeAsync(message.buildPhoto());
        } else {
            bot.executeAsync(message.build());
        }
    }

    @SneakyThrows
    public void answer(String text, Object... args) {
        AbstractMessage message = createMessage(text, args);
        if (message.hasPhoto()) {
            bot.execute(message.buildPhoto());
        } else {
            bot.execute(message.build());
        }
    }

    private AbstractMessage createMessage(String text, Object... args) {
        AbstractMessage message = createBuilder(text);
        for (Object arg : args) {
            switch (arg) {
                case InlineKeyboardMarkup keyboard -> message.keyboard(keyboard);
                case InputFile photo -> message.photo(photo);
                case String parseMode -> message.parseMode(parseMode);
                default -> {
                    // Pass if argument is a random value
                }
            }
        }
        return message;
    }

    private AbstractMessage createBuilder(String text) {
        return AbstractMessage
                .builder()
                .id(message.getChatId())
                .text(text);
    }

    private static Message getRawMessage(long userId) {
        Chat chat = new Chat();
        chat.setId(userId);

        Message message = new Message();
        message.setChat(chat);
        message.setText("");

        return message;
    }
}