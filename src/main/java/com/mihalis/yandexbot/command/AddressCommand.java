package com.mihalis.yandexbot.command;

import com.mihalis.yandexbot.cache.AddressState;
import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.telegram.Parcel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.mihalis.yandexbot.data.StringMessages.enterNewDeliveryAddress;

@Component
public class AddressCommand extends Command {
    private final AddressRepository addressesCache;
    private final AddressState addressState;

    private final InlineKeyboardMarkup cancelKeyboard;

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
        parcel.answerAsync(enterNewDeliveryAddress, cancelKeyboard);

        addressState.setActive(parcel.getUserId(), true);
    }

    private void sendCurrentAddress(Parcel parcel) {
        Address address = addressesCache.getAddress(parcel.getUserId());
        if (address.notEmpty()) {
            parcel.answer("Твой текущий адрес: " + address.getOriginalAddress());
        }
    }
}
