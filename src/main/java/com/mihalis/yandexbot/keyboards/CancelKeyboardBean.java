package com.mihalis.yandexbot.keyboards;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Configuration
class CancelKeyboardBean {
    @Bean(name = "cancelKeyboard")
    public InlineKeyboardMarkup getCancelKeyboard() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Отмена");
        button.setCallbackData("cancel");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(getListsWrap(button));

        return keyboard;
    }

    private List<List<InlineKeyboardButton>> getListsWrap(InlineKeyboardButton button) {
        val list1 = new ArrayList<List<InlineKeyboardButton>>();
        val list2 = new ArrayList<InlineKeyboardButton>();
        list2.add(button);
        list1.add(list2);

        return list1;
    }
}
