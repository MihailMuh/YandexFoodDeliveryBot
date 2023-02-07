package com.mihalis.yandexbot.handler;

import com.mihalis.yandexbot.cache.AddressState;
import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.selenium.exceptions.NoDeliveryException;
import com.mihalis.yandexbot.selenium.exceptions.TooMuchAttemptsException;
import com.mihalis.yandexbot.service.YandexFoodService;
import com.mihalis.yandexbot.telegram.Parcel;
import com.mihalis.yandexbot.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.ExecutorService;

@Component
@AllArgsConstructor
public class NewAddressHandler implements Handler {
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
        if (StringUtils.countMatches(newAddress, ",") < 2 ||
                !StringUtils.containsDigits(newAddress) ||
                newAddress.charAt(0) == ',' || newAddress.charAt(newAddress.length() - 1) == ',' ||
                newAddress.contains(",,")) {

            parcel.answerAsync("Неверный формат адреса!");
            return;
        }

        parcel.answerAsync("Смотрю стоимость доставки... Это займет около минуты, наберись терпения");
        executorService.execute(() -> processBrowser(parcel, Address.of(newAddress)));
    }

    @SneakyThrows
    private void processBrowser(Parcel parcel, Address newAddress) {
        long id = parcel.getUserId();

        try {
            if (addressRepository.hasAddress(id)) {
                yandexFoodService.updateAddress(id, newAddress);
            } else {
                yandexFoodService.createNewAddress(id, newAddress);
            }
            parcel.answerAsync(yandexFoodService.getDeliveryCost(id));

            addressState.setActive(id, false);
            addressRepository.setAddress(id, newAddress);
            return;
        } catch (NoDeliveryException noDeliveryException) {
            parcel.answer("Кажется, по этому адресу нет доставки", screenshot(id));
        } catch (TooMuchAttemptsException tooMuchAttemptsException) {
            parcel.answer("Что-то я не смог найти твой адрес", screenshot(id));
        } catch (Exception e) {
            e.printStackTrace();
            parcel.answer("Что-то нечего не получилось узнать...", screenshot(id));
        }

        parcel.answerAsync("Я мог в чем-то ошибиться. Чтобы повторить поиск, введи еще раз адрес\n" +
                "Для отмены нажми кнопку ОТМЕНА в сообщении выше");
        yandexFoodService.cancelAddress(id);
    }

    private InputFile screenshot(long userId) {
        return yandexFoodService.takeScreenshot(userId);
    }
}
