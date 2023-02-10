package com.mihalis.yandexbot.utils;

import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@ToString
public class Keyboard {
    private final InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

    private final ArrayList<List<InlineKeyboardButton>> buttonsVertical = new ArrayList<>();

    private ArrayList<InlineKeyboardButton> buttonsHorizontal = new ArrayList<>();

    public void addButton(String buttonText, String buttonCallbackName, boolean horizontalPosition) {
        if (!horizontalPosition) {
            flushHorizontalButtons();
            addButton(buttonText, buttonCallbackName, true);
            flushHorizontalButtons();
            return;
        }

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonText);
        button.setCallbackData(buttonCallbackName);

        buttonsHorizontal.add(button);
    }

    public InlineKeyboardMarkup getKeyboard() {
        flushHorizontalButtons();
        keyboard.setKeyboard(buttonsVertical);
        return keyboard;
    }

    private void flushHorizontalButtons() {
        if (!buttonsHorizontal.isEmpty()) {
            buttonsVertical.add(buttonsHorizontal);
            buttonsHorizontal = new ArrayList<>();
        }
    }
}
