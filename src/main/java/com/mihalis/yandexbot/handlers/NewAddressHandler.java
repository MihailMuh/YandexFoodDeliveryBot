package com.mihalis.yandexbot.handlers;

import com.mihalis.yandexbot.cache.Address;
import com.mihalis.yandexbot.cache.AddressCache;
import com.mihalis.yandexbot.cache.SelectNewAddressCache;
import com.mihalis.yandexbot.selenium.YandexFood;
import com.mihalis.yandexbot.selenium.exceptions.AttemptException;
import com.mihalis.yandexbot.selenium.exceptions.NoDeliveryException;
import com.mihalis.yandexbot.telegram.Bot;
import com.mihalis.yandexbot.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@AllArgsConstructor
public class NewAddressHandler {
    private final SelectNewAddressCache selectNewAddressCache;

    private final AddressCache addressCache;

    private final YandexFood yandexFood;

    public boolean handleUpdate(Bot bot, Message message) {
        // sent random message
        if (!selectNewAddressCache.isActiveNewAddressOperation(message.getChatId())) {
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

        bot.executeAsync("Смотрю стоимость доставки... Это займет около 15 секунд", message);

        new Thread(() -> processBrowser(bot, message, Address.of(newAddress))).start();
        return true;
    }

    @SneakyThrows
    private void processBrowser(Bot bot, Message message, Address newAddress) {
        try {
            String cost = yandexFood.getDeliveryCost(newAddress, message.getChatId());
            bot.executeAsync(cost, message);

            selectNewAddressCache.setActivatedNewAddressOperation(message.getChatId(), false);
            addressCache.setAddress(message.getChatId(), newAddress);
            return;
        } catch (NoDeliveryException noDeliveryException) {
            bot.execute(getPhoto("Кажется, по этому адресу нет доставки", message));
        } catch (AttemptException attemptException) {
            bot.execute(getPhoto("Что-то я не смог найти твой адрес", message));
        } catch (Exception e) {
            e.printStackTrace();
            bot.execute("Что-то нечего не получилось узнать...", message);
        }

        bot.executeAsync("Я мог в чем-то ошибиться. Чтобы повторить поиск, введи еще раз адрес\n" +
                "Для отмены нажми кнопку ОТМЕНА в сообщении выше", message);
    }

    private SendPhoto getPhoto(String text, Message message) {
        SendPhoto sendPhoto = new SendPhoto(message.getChatId().toString(), yandexFood.getScreenshot());
        sendPhoto.setCaption(text);

        return sendPhoto;
    }
}
