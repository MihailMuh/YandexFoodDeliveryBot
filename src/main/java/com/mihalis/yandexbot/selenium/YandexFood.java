package com.mihalis.yandexbot.selenium;

import com.mihalis.yandexbot.cache.Address;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@Log4j2
@Component
@AllArgsConstructor
public class YandexFood {
    private final DeliveryAddressPage deliveryAddressPage;

    public InputFile getScreenshot() {
        return deliveryAddressPage.screenshot();
    }

    public String getDeliveryCost(Address userAddress, long userId) {
        deliveryAddressPage.init(userAddress, userId);

        enterDeliveryAddress();

        return processCost(deliveryAddressPage.getDeliveryCost());
    }

    private void enterDeliveryAddress() {
        deliveryAddressPage.clickSetAddressBtn();

        deliveryAddressPage.clearDeliveryAddress();

        deliveryAddressPage.inputNewAddress();
        deliveryAddressPage.applyNewAddress();
    }

    private String processCost(String deliveryCost) {
        log.info(deliveryCost);

        return deliveryCost;
    }
}
