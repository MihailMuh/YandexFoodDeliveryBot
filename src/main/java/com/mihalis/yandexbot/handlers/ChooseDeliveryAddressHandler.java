package com.mihalis.yandexbot.handlers;

import com.mihalis.yandexbot.cache.AddressState;
import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.service.YandexFoodService;
import com.mihalis.yandexbot.telegram.Bot;
import com.mihalis.yandexbot.utils.AddressValidator;
import com.mihalis.yandexbot.utils.Keyboard;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.mihalis.yandexbot.data.StringMessages.chooseDeliveryAddress;

@Component
@AllArgsConstructor
public class ChooseDeliveryAddressHandler {
    private final AddressState addressState;

    private final AddressRepository addressRepository;

    private final YandexFoodService yandexFoodService;

    private final ExecutorService executorService;

    public boolean handleUpdate(Bot bot, Message message) {
        // if message is randomly
        if (!addressState.isActive(message.getChatId())) {
            return false;
        }

        String newAddress = message.getText();
        if (AddressValidator.isValid(newAddress)) {
            bot.executeAsync("Неверный формат адреса!", message);
            return true;
        }

        bot.executeAsync("Сверяю этот адрес с Яндекс картами...", message);

        executorService.execute(() -> processBrowser(bot, message, Address.of(newAddress)));
        return true;
    }

    @SneakyThrows
    private void processBrowser(Bot bot, Message message, Address newAddress) {
        long id = message.getChatId();

        if (addressRepository.hasAddress(id)) {
            yandexFoodService.updateAddress(id, newAddress);
        } else {
            yandexFoodService.createNewAddress(id, newAddress);
        }

        Keyboard addressesKeyboard = generateAddressesKeyboard(yandexFoodService.getDeliveryAddresses(id));
        bot.executeAsync(chooseDeliveryAddress, message, addressesKeyboard.getKeyboard());
    }

    private Keyboard generateAddressesKeyboard(List<String> deliveryAddresses) {
        Keyboard addressesKeyboard = new Keyboard();

        for (String deliveryAddress : deliveryAddresses) {
            addressesKeyboard.addButton(deliveryAddress, "deliveryAddress");
        }
        addressesKeyboard.addButton("Повторить", "refresh");
        addressesKeyboard.addButton("Отмена", "cancel");

        return addressesKeyboard;
    }
}
