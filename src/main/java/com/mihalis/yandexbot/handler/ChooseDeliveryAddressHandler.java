package com.mihalis.yandexbot.handler;

import com.mihalis.yandexbot.cache.AddressState;
import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.service.YandexFoodService;
import com.mihalis.yandexbot.telegram.Parcel;
import com.mihalis.yandexbot.utils.AddressValidator;
import com.mihalis.yandexbot.utils.Keyboard;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.mihalis.yandexbot.data.StringMessages.chooseDeliveryAddress;

@Component
@AllArgsConstructor
public class ChooseDeliveryAddressHandler implements Handler {
    private final AddressState addressState;

    private final AddressRepository addressRepository;

    private final YandexFoodService yandexFoodService;

    private final ExecutorService executorService;

    @Override
    public boolean relevantCondition(Message message) {
        return addressState.isActive(message.getChatId());
    }

    @Override
    public void handleParcel(Parcel parcel) {
        String newAddress = parcel.getText();
        if (AddressValidator.isValid(newAddress)) {
            parcel.answerAsync("Неверный формат адреса!");
            return;
        }

        parcel.answerAsync("Смотрю стоимость доставки... Это займет около минуты, наберись терпения");
        executorService.execute(() -> answerWithKeyboardFromBrowser(parcel, Address.of(newAddress)));
    }

    private void answerWithKeyboardFromBrowser(Parcel parcel, Address newAddress) {
        long id = parcel.getUserId();

        if (addressRepository.hasAddress(id)) {
            yandexFoodService.updateAddress(id, newAddress);
        } else {
            yandexFoodService.createNewAddress(id, newAddress);
        }

        Keyboard addressesKeyboard = generateAddressesKeyboard(yandexFoodService.getDeliveryAddresses(id));
        parcel.answerAsync(chooseDeliveryAddress, addressesKeyboard.getKeyboard());
    }

    private Keyboard generateAddressesKeyboard(List<String> deliveryAddresses) {
        Keyboard addressesKeyboard = new Keyboard();

        for (String deliveryAddress : deliveryAddresses) {
            addressesKeyboard.addButton(deliveryAddress, "deliveryAddress", false);
        }
        addressesKeyboard.addButton("Повторить", "refresh", true);
        addressesKeyboard.addButton("Отмена", "cancel", true);

        return addressesKeyboard;
    }
}