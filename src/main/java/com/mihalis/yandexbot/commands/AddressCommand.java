package com.mihalis.yandexbot.commands;

import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.cache.AddressState;
import com.mihalis.yandexbot.telegram.Bot;
import com.mihalis.yandexbot.telegram.PostMessage;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class AddressCommand extends BaseCommand {
    private final AddressRepository addressesCache;
    private final AddressState addressState;

    private final InlineKeyboardMarkup cancelKeyboard;

    private static final String conditionMessage =
            "Новый адрес должен быть в формате:\n" +
                    "Город, улица, дом\n\n" +
                    "Например:\n" +
                    "Екатеринбург, улица Малышева, 53А\n\n" +
                    "Вводи адрес, соблюдая этот формат! Если в течение 5 попыток я не найду этот адрес в " +
                    "яндекс картах, будешь вводить по-новой\n\n" +
                    "Я буду писать тебе стоимость доставки по указанному адресу каждые 15 минут. " +
                    "Когда надоест, вызови /stop";

    public AddressCommand(AddressRepository addressRepository, InlineKeyboardMarkup cancelKeyboard,
                          AddressState addressState) {
        super("address");
        this.addressesCache = addressRepository;
        this.addressState = addressState;
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

        addressState.setActive(message.getChatId(), true);
    }

    private void sendCurrentAddress(Bot bot, Message message) {
        Address address = addressesCache.getAddress(message.getChatId());
        if (address.notEmpty()) {
            bot.execute("Твой текущий адрес: " + address.getOriginalAddress(), message);
        }
    }
}
