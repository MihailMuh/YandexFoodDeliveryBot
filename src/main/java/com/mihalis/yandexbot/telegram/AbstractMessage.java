package com.mihalis.yandexbot.telegram;

import lombok.*;
import lombok.experimental.Accessors;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static lombok.AccessLevel.PRIVATE;

@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = PRIVATE)
@Accessors(fluent = true, chain = true)
class AbstractMessage {
    private String text;

    private long id;

    private InlineKeyboardMarkup keyboard;

    private InputFile photo;

    private String parseMode;

    public boolean hasPhoto() {
        return photo != null;
    }

    public SendMessage build() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboard);
        sendMessage.setParseMode(parseMode);

        return sendMessage;
    }

    public SendPhoto buildPhoto() {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(id);
        sendPhoto.setCaption(text);
        sendPhoto.setReplyMarkup(keyboard);
        sendPhoto.setParseMode(parseMode);
        sendPhoto.setPhoto(photo);

        return sendPhoto;
    }

    public static AbstractMessage builder() {
        return new AbstractMessage();
    }
}
