package com.mihalis.yandexbot.service;

import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.model.DeliveryData;
import com.mihalis.yandexbot.repository.PageRepository;
import com.mihalis.yandexbot.selenium.BrowserPage;
import com.mihalis.yandexbot.utils.Timer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class YandexFoodService {
    private final PageRepository pageRepository;

    private final Timer timer;

    public void runScheduledCheckOfCost(Consumer<DeliveryData> costConsumer) {
        timer.start(() -> pageRepository.iterateDeliveryCosts(costConsumer));
    }

    public void createNewAddress(long userId, Address address) {
        pageRepository.createPage(userId, address);
    }

    public void updateAddress(long userId, Address address) {
        pageRepository.updatePage(userId, address);
    }

    public void deleteAddress(long userId) {
        pageRepository.deletePage(userId);
    }

    public InputFile takeScreenshot(long userId) {
        return getBrowserPage(userId).screenshot();
    }

    public String getDeliveryCost(long userId) {
        return getBrowserPage(userId).getDeliveryCost();
    }

    private BrowserPage getBrowserPage(long userId) {
        return pageRepository.getPage(userId);
    }
}
