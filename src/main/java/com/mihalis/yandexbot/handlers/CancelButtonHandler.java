package com.mihalis.yandexbot.handlers;

import com.mihalis.yandexbot.cache.AddressState;
import com.mihalis.yandexbot.telegram.Bot;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@AllArgsConstructor
public class CancelButtonHandler {
    private final AddressState addressState;

    @SneakyThrows
    public void handleUpdate(Bot bot, CallbackQuery callback) {
        if (!"cancel".equals(callback.getData())) {
            return;
        }

        bot.executeAsync("Операция отменена", callback.getMessage());

        addressState.setActive(callback.getMessage().getChatId(), false);
    }
}
