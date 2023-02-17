package com.mihalis.yandexbot.selenium;

import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.selenium.exceptions.NoDeliveryException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.Keys.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class BrowserPage extends Page {
    @Getter
    private Address userAddress;

    private Logger log;

    private WebElement inputAddress;

    private boolean young = true;

    public BrowserPage(String yandexUrl, WebDriver browser) {
        super(yandexUrl, browser);

        log = LoggerFactory.getLogger(getClass().getSimpleName());
        log.info("Browser created!");

        init();
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
        boolean addressAlreadyEntered = (boolean) javaScript.executeScript("""
                    return document.querySelector("div[class='shown cc9u8rz']") === null;
                """);

        if (addressAlreadyEntered) {
            browser.findElement(cssSelector("html > body > div > header > div:nth-of-type(5) > button")).click();
        } else {
            browser.findElement(cssSelector("div[class='shown cc9u8rz']")).click();
        }
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
        if (!young) {
            refreshPage();
            log.info("refresh");
        } else {
            young = false;
        }

        By deliveryCostDiv = cssSelector("div[class='t1vrfrqt t18stym3 bw441np r88klks r1dbrdpx n10d4det l14lhr1r']");
        WebElement deliveryCost = Wait().until(visibilityOfElementLocated(deliveryCostDiv));

        log.info("Delivery cost received");
        return deliveryCost.getText();
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
