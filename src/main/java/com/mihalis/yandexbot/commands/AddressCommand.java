package com.mihalis.yandexbot.commands;

import com.mihalis.yandexbot.cache.AddressCache;
import com.mihalis.yandexbot.cache.SelectNewAddressCache;
import com.mihalis.yandexbot.telegram.Bot;
import com.mihalis.yandexbot.telegram.PostMessage;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class AddressCommand extends BaseCommand {
    private final AddressCache addressesCache;
    private final SelectNewAddressCache selectNewAddressCache;

    private final InlineKeyboardMarkup cancelKeyboard;

    private static final String conditionMessage = "Новый адрес должен быть в формате:\nГород, улица, дом";

    public AddressCommand(AddressCache addressCache, InlineKeyboardMarkup cancelKeyboard,
                          SelectNewAddressCache selectNewAddressCache) {
        super("address");
        this.addressesCache = addressCache;
        this.selectNewAddressCache = selectNewAddressCache;
        this.cancelKeyboard = cancelKeyboard;
    }

    @Override
    @SneakyThrows
    public void answer(Bot bot, Message message) {
        sendCurrentAddress(bot, message);

        PostMessage sendMessage = new PostMessage(message);
        sendMessage.setText(conditionMessage);
        sendMessage.setReplyMarkup(cancelKeyboard);

        bot.executeAsync(sendMessage);

        selectNewAddressCache.setActivatedNewAddressOperation(message.getChatId(), true);
    }

    private void sendCurrentAddress(Bot bot, Message message) {
        String currentAddress = addressesCache.getAddress(message.getChatId()).address();
        if (currentAddress.length() != 0) {
            bot.executeAsync("Твой текущий адрес: " + currentAddress, message);
        }
    }
}
