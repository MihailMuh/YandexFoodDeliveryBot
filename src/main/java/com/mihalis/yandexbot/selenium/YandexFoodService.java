package com.mihalis.yandexbot.selenium;

import com.mihalis.yandexbot.cache.Address;
import com.mihalis.yandexbot.utils.DeliveryData;
import com.mihalis.yandexbot.utils.Timer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.function.Consumer;

@Log4j2
@Service("yandexFoodService")
@AllArgsConstructor
public class YandexFoodService {
    private final PageRepository pageRepository;

    private final Timer timer;

    public void runScheduledCheckOfCost(Consumer<DeliveryData> costConsumer) {
        timer.start(() -> {
            for (DeliveryData deliveryData : pageRepository.getAllDeliveryCosts()) {
                costConsumer.accept(deliveryData);
            }
        });
    }

    public void setNewAddress(long userId, Address address) {
        pageRepository.setNewPage(userId, address);
    }

    public void deleteAddress(long userId) {
        pageRepository.deletePage(userId);
    }

    public InputFile takeScreenshot(long userId) {
        return pageRepository.takeScreenshot(userId);
    }

    public String getDeliveryCost(long userId) {
        return pageRepository.getDeliveryCost(userId);
    }
}
