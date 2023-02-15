package com.mihalis.yandexbot.command;

import com.mihalis.yandexbot.cache.FiniteStateMachine;
import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.telegram.Parcel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.mihalis.yandexbot.data.StringMessages.enterNewDeliveryAddress;

@Component
public class AddressCommand extends Command {
    private final AddressRepository addressesCache;
    private final FiniteStateMachine finiteStateMachine;

    private final InlineKeyboardMarkup cancelKeyboard;

    public AddressCommand(AddressRepository addressRepository, InlineKeyboardMarkup cancelKeyboard,
                          FiniteStateMachine finiteStateMachine) {
        super("address");
        this.addressesCache = addressRepository;
        this.finiteStateMachine = finiteStateMachine;
        this.cancelKeyboard = cancelKeyboard;
    }

    @Override
    public void answer(Parcel parcel) {
        sendCurrentAddress(parcel);
        parcel.answerAsync(enterNewDeliveryAddress, cancelKeyboard);

        finiteStateMachine.setValue(parcel.getUserId(), "dialogState", "chooseDeliveryAddress");
    }

    private void sendCurrentAddress(Parcel parcel) {
        Address address = addressesCache.getAddress(parcel.getUserId());
        if (address.notEmpty()) {
            parcel.answer("Твой текущий адрес: " + address.getOriginalAddress());
        }
    }
}
