package com.mihalis.yandexbot.utils;

import lombok.Getter;
import lombok.val;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Keyboard {
    @Getter
    private final InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

    private final ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();

    public Keyboard() {
        val list = new ArrayList<List<InlineKeyboardButton>>();
        list.add(buttons);

        keyboard.setKeyboard(list);
    }

    public void addButton(String buttonText, String buttonCallbackName) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonText);
        button.setCallbackData(buttonCallbackName);

        buttons.add(button);
    }
}
