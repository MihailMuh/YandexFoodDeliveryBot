package com.mihalis.yandexbot.callback;

import com.mihalis.yandexbot.cache.FiniteStateMachine;
import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.selenium.exceptions.NoDeliveryException;
import com.mihalis.yandexbot.service.YandexFoodService;
import com.mihalis.yandexbot.telegram.Parcel;
import com.mihalis.yandexbot.utils.Keyboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.concurrent.ExecutorService;

import static java.lang.Integer.parseInt;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryAddressCallback implements Callback {
    private final FiniteStateMachine finiteStateMachine;
    private final AddressRepository addressRepository;

    private final YandexFoodService yandexFoodService;

    private final ExecutorService executorService;

    private int addressRowIndex;

    @Override
    public boolean relevantCondition(CallbackQuery callback) {
        if (callback.getData().startsWith("deliveryAddress_") &&
                finiteStateMachine.hasState(callback.getMessage().getChatId())) {

            addressRowIndex = parseInt(callback.getData().split("deliveryAddress_")[1]);
            return true;
        }
        return false;
    }

    @Override
    public void handleParcel(Parcel parcel) {
        Address userAddress = (Address) finiteStateMachine.getValue(parcel.getUserId(), "userAddress");

        parcel.answerAsync("Смотрю стоимость доставки...");

        executorService.execute(() ->
        {
            try {
                answerDeliveryCost(parcel, addressRowIndex, userAddress.getOriginalAddress());
            } catch (NoDeliveryException e) {
                answerNoDeliveryAddress(parcel);
            }
        });
    }

    private void answerDeliveryCost(Parcel parcel, int addressRawIndex, String address) throws NoDeliveryException {
        long id = parcel.getUserId();

        parcel.answerAsync(address + "\n" + yandexFoodService.getDeliveryCost(id, addressRawIndex));

        addressRepository.setAddress(id, Address.of(address));
        finiteStateMachine.delete(id);
    }

    private void answerNoDeliveryAddress(Parcel parcel) {
        parcel.answer("Кажется сюда нет доставки...", getKeyboard(),
                yandexFoodService.getScreenshot(parcel.getUserId()));
    }

    private InlineKeyboardMarkup getKeyboard() {
        Keyboard addressesKeyboard = new Keyboard();

        addressesKeyboard.addButton("Повторить", "refresh", true);
        addressesKeyboard.addButton("Отмена", "cancel", true);

        return addressesKeyboard.getKeyboard();
    }
}
