package com.mihalis.yandexbot.handlers;

import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.cache.AddressState;
import com.mihalis.yandexbot.service.YandexFoodService;
import com.mihalis.yandexbot.selenium.exceptions.TooMuchAttemptsException;
import com.mihalis.yandexbot.selenium.exceptions.NoDeliveryException;
import com.mihalis.yandexbot.telegram.Bot;
import com.mihalis.yandexbot.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.ExecutorService;

@Component
@AllArgsConstructor
public class NewAddressHandler {
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
        if (StringUtils.countMatches(newAddress, ",") < 2 ||
                !StringUtils.containsDigits(newAddress) ||
                newAddress.charAt(0) == ',' || newAddress.charAt(newAddress.length() - 1) == ',' ||
                newAddress.contains(",,")) {

            bot.executeAsync("Неверный формат адреса!", message);
            return true;
        }

        bot.executeAsync("Смотрю стоимость доставки... Это займет около минуты, наберись терпения", message);

        executorService.execute(() -> processBrowser(bot, message, Address.of(newAddress)));
        return true;
    }

    @SneakyThrows
    private void processBrowser(Bot bot, Message message, Address newAddress) {
        long id = message.getChatId();

        try {
            if (addressRepository.hasAddress(id)) {
                yandexFoodService.updateAddress(id, newAddress);
            } else {
                yandexFoodService.createNewAddress(id, newAddress);
            }
            String cost = yandexFoodService.getDeliveryCost(id);
            bot.executeAsync(cost, message);

            addressState.setActive(id, false);
            addressRepository.setAddress(id, newAddress);
            return;
        } catch (NoDeliveryException noDeliveryException) {
            bot.execute(createPhoto("Кажется, по этому адресу нет доставки", id));
        } catch (TooMuchAttemptsException tooMuchAttemptsException) {
            bot.execute(createPhoto("Что-то я не смог найти твой адрес", id));
        } catch (Exception e) {
            e.printStackTrace();
            bot.execute(createPhoto("Что-то нечего не получилось узнать...", id));
        }

        bot.executeAsync("Я мог в чем-то ошибиться. Чтобы повторить поиск, введи еще раз адрес\n" +
                "Для отмены нажми кнопку ОТМЕНА в сообщении выше", message);
        yandexFoodService.cancelAddress(id);
    }

    private SendPhoto createPhoto(String text, long userId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(yandexFoodService.takeScreenshot(userId));
        sendPhoto.setChatId(userId);
        sendPhoto.setCaption(text);

        return sendPhoto;
    }
}
