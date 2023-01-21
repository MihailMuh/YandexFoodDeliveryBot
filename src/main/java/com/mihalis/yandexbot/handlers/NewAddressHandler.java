package com.mihalis.yandexbot.handlers;

import com.mihalis.yandexbot.cache.SelectNewAddressCache;
import com.mihalis.yandexbot.telegram.Bot;
import com.mihalis.yandexbot.utils.StringUtils;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class NewAddressHandler {
    @Setter(value = AccessLevel.PRIVATE, onMethod_ = {@Autowired})
    private SelectNewAddressCache selectNewAddressCache;

    public boolean handleUpdate(Bot bot, Message message) {
        // sent random message
        if (!selectNewAddressCache.isActiveNewAddressOperation(message.getChatId())) {
            return false;
        }

        String newAddress = message.getText();
        if (StringUtils.countMatches(newAddress, ",") < 2 || !StringUtils.containsDigits(newAddress)) {
            bot.executeAsync("Неверный формат адреса!", message);
            return true;
        }

        bot.executeAsync("Верно!", message);
        selectNewAddressCache.setActivatedNewAddressOperation(message.getChatId(), false);
        return true;
    }
}
