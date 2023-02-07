package com.mihalis.yandexbot.command;

import com.mihalis.yandexbot.cache.AddressState;
import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.telegram.Parcel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class AddressCommand extends Command {
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
    public void answer(Parcel parcel) {
        sendCurrentAddress(parcel);
        parcel.answerAsync(conditionMessage, cancelKeyboard);

        addressState.setActive(parcel.getUserId(), true);
    }

    private void sendCurrentAddress(Parcel parcel) {
        Address address = addressesCache.getAddress(parcel.getUserId());
        if (address.notEmpty()) {
            parcel.answer("Твой текущий адрес: " + address.getOriginalAddress());
        }
    }
}
