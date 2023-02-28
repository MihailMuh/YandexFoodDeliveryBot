package com.mihalis.yandexbot.selenium;

import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.model.BrowserProfile;
import com.mihalis.yandexbot.selenium.exceptions.NoDeliveryException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.Keys.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class BrowserPage extends Page {
    @Getter
    private Address userAddress;

    private WebElement inputAddress;

    private boolean firstConfirm = true;

    public BrowserPage(String yandexUrl, WebDriver browser, BrowserProfile profile) {
        super(yandexUrl, browser, profile);

        if (profileIsNew()) {
            init();
        }
    }

    @Override
    protected void init() {
        clickNewAddressButton();
        clearOldAddress();
    }

    public void update(long userId, Address userAddress) {
        this.userAddress = userAddress;
        log = LoggerFactory.getLogger(String.valueOf(userId));
    }

    public void clickNewAddressButton() {
        browser.findElement(cssSelector("html > body > div > header > div:nth-of-type(5) > button")).click();
        log.info("NewAddressButton clicked");
    }

    @SneakyThrows
    public void clearOldAddress() {
        // while maps loads location, we just wait
        Thread.sleep(700);

        inputAddress = Wait().until(elementToBeClickable(cssSelector("input[class='i164506l']")));
        inputAddress.click();
        inputAddress.sendKeys(CONTROL + "a");
        inputAddress.sendKeys(DELETE);

        log.info("Delivery address cleared");
    }

    @SneakyThrows
    public void inputNewAddress() {
        String address = userAddress.getOriginalAddress();
        for (int i = 0; i < address.length(); i++) {
            inputAddress.sendKeys(address.substring(i, i + 1)); // one symbol
            Thread.sleep(250);
        }

        log.info("New address entered");
    }

    public void applyAddress(int addressRowIndex) throws NoDeliveryException {
        for (int i = 0; i < addressRowIndex + 1; i++) {
            inputAddress.sendKeys(DOWN);
        }
        inputAddress.sendKeys(ENTER);

        waitForYmaps();
        try {
            By okButtonScc = cssSelector("button[class='bzscopr f19ph74x c14xrn6c cow0qbn a71den4 m16coeem m1wd6zeg w3gf8dt']");
            Wait(5).until(presenceOfElementLocated(okButtonScc)).click();

            log.info("New address confirmed");
        } catch (Exception exception) {
            throw new NoDeliveryException(exception);
        }
    }

    public String getDeliveryCost() {
        if (!firstConfirm) {
            refreshPage();
            log.info("refresh");
        } else {
            firstConfirm = false;
        }

        Wait().until(visibilityOfElementLocated(cssSelector("button[class='d1tj8rdb']"))).click();

        String[] deliveryInfoRaw = Wait().until(visibilityOfElementLocated(cssSelector("ul[class='cnw6bwb c3uyybh']"))).getText().split("\n");

        for (String s : deliveryInfoRaw) {
            System.out.println(s + " ----------");
        }

        String deliveryData = """
                
                Условия доставки:
                    %s %s
                    **%s**
                """.formatted(deliveryInfoRaw[0], deliveryInfoRaw[1], deliveryInfoRaw[2]);

        log.info("Delivery cost received");
        return deliveryData;
    }

    @SneakyThrows
    public List<String> getDeliveryAddresses() {
        // wait while all addresses in the list box are updated
        Thread.sleep(1000);

        WebElement listBox = browser.findElement(cssSelector("ul[class='l1xltboq']"));
        String[] rawAddresses = listBox.getText().split("\n");
        ArrayList<String> addresses = new ArrayList<>();

        for (int i = 0; i < rawAddresses.length - 1; i += 2) {
            String street = rawAddresses[i];
            String townAndState = rawAddresses[i + 1]; // something like this: "Екатеринбург, Свердловская область"
            String town = townAndState.split(", ")[0];

            addresses.add(town + ", " + street);
        }

        log.info("Delivery addresses collected");

        return addresses;
    }

    private void waitForYmaps() {
        // wait while ymaps are getting coordinates
        Wait(60).until(elementToBeClickable(cssSelector("ymaps[class='ymaps-2-1-79-map']")));
    }
}
