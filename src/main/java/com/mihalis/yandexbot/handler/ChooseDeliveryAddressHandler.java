package com.mihalis.yandexbot.handler;

import com.mihalis.yandexbot.cache.FiniteStateMachine;
import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.repository.AddressRepository;
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

    private final AddressRepository addressRepository;

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
        finiteStateMachine.setValue(parcel.getUserId(), "userAddress", newAddress);
    }

    public void answerWithKeyboardFromBrowser(Parcel parcel, Address newAddress) {
        executorService.execute(() -> {
            long id = parcel.getUserId();
            if (addressRepository.hasAddress(id)) {
                yandexFoodService.updateAddress(id, newAddress);
            } else {
                yandexFoodService.createNewAddress(id, newAddress);
            }

            Keyboard addressesKeyboard = generateAddressesKeyboard(yandexFoodService.getDeliveryAddresses(id));
            parcel.answerAsync(chooseDeliveryAddress, addressesKeyboard.getKeyboard());
        });

        parcel.answerAsync("Сверяю этот адрес с Яндекс картами... Это займет около половины минуты");
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
