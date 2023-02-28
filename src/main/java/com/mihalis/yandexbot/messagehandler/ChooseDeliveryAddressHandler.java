package com.mihalis.yandexbot.messagehandler;

import com.mihalis.yandexbot.cache.FiniteStateMachine;
import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.service.YandexFoodService;
import com.mihalis.yandexbot.telegram.Parcel;
import com.mihalis.yandexbot.utils.AddressValidator;
import com.mihalis.yandexbot.utils.Keyboard;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.mihalis.yandexbot.data.StringMessages.chooseDeliveryAddress;

@Slf4j
@Component
@AllArgsConstructor
public class ChooseDeliveryAddressHandler implements Handler {
    private final FiniteStateMachine finiteStateMachine;

    private final YandexFoodService yandexFoodService;

    private final ExecutorService executorService;

    @Override
    public boolean relevantCondition(Message message) {
        return "chooseDeliveryAddress".equals(finiteStateMachine.getValue(message.getChatId(), "dialogState"));
    }

    @Override
    public void handleParcel(Parcel parcel) {
        String newAddress = parcel.getText();
        if (AddressValidator.isValid(newAddress)) {
            parcel.answerAsync("Неверный формат адреса!");
            return;
        }

        answerWithKeyboardFromBrowser(parcel, Address.of(newAddress));
        parcel.answerAsync("Сверяю этот адрес с Яндекс картами... Это займет около половины минуты");

        finiteStateMachine.setValue(parcel.getUserId(), "userAddress", Address.of(newAddress));
    }

    public void answerWithKeyboardFromBrowser(Parcel parcel, Address newAddress) {
        executorService.execute(() -> {
            long id = parcel.getUserId();

            // delete old page associated for this user id
            // page deleting happens only there
            yandexFoodService.deleteAddress(id);
            yandexFoodService.createNewAddress(id, newAddress);

            Keyboard addressesKeyboard = generateAddressesKeyboard(yandexFoodService.getDeliveryAddresses(id));
            parcel.answerAsync(chooseDeliveryAddress, addressesKeyboard.getKeyboard());
        });
    }

    private Keyboard generateAddressesKeyboard(List<String> deliveryAddresses) {
        Keyboard addressesKeyboard = new Keyboard();

        for (int i = 0; i < deliveryAddresses.size(); i++) {
            String deliveryAddress = deliveryAddresses.get(i);
            addressesKeyboard.addButton(deliveryAddress, "deliveryAddress_" + i, false);
        }

        addressesKeyboard.addButton("Повторить", "refresh", true);
        addressesKeyboard.addButton("Отмена", "cancel", true);

        return addressesKeyboard;
    }
}
