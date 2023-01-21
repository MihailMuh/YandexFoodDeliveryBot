package com.mihalis.yandexbot.handlers;

import com.mihalis.yandexbot.cache.SelectNewAddressCache;
import com.mihalis.yandexbot.telegram.Bot;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@Log4j2
public class CancelButtonHandler {
    @Setter(value = AccessLevel.PRIVATE, onMethod_ = {@Autowired})
    private SelectNewAddressCache selectNewAddressCache;

    @SneakyThrows
    public void handleUpdate(Bot bot, CallbackQuery callback) {
        if (!"cancel".equals(callback.getData())) {
            return;
        }

        bot.executeAsync("Операция отменена", callback.getMessage());

        selectNewAddressCache.setActivatedNewAddressOperation(callback.getMessage().getChatId(), false);
    }
}
