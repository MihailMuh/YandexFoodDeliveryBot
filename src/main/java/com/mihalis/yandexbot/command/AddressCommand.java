package com.mihalis.yandexbot.command;

import com.mihalis.yandexbot.cache.FiniteStateMachine;
import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.telegram.Parcel;
import com.mihalis.yandexbot.utils.Keyboard;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.mihalis.yandexbot.data.StringMessages.enterNewDeliveryAddress;

@Component
public class AddressCommand extends Command {
    private final AddressRepository addressRepository;
    private final FiniteStateMachine finiteStateMachine;

    public AddressCommand(AddressRepository addressRepository, FiniteStateMachine finiteStateMachine) {
        super("address");
        this.addressRepository = addressRepository;
        this.finiteStateMachine = finiteStateMachine;
    }

    @Override
    public void answer(Parcel parcel) {
        sendCurrentAddress(parcel);
        parcel.answerAsync(enterNewDeliveryAddress, getCancelKeyboard());

        finiteStateMachine.setValue(parcel.getUserId(), "dialogState", "chooseDeliveryAddress");
    }

    private void sendCurrentAddress(Parcel parcel) {
        Address address = addressRepository.getAddress(parcel.getUserId());
        if (address != null) {
            parcel.answer("Твой текущий адрес: " + address.getOriginalAddress());
        }
    }

    private InlineKeyboardMarkup getCancelKeyboard() {
        Keyboard keyboard = new Keyboard();
        keyboard.addButton("Отмена", "cancel", true);
        return keyboard.getKeyboard();
    }
}
