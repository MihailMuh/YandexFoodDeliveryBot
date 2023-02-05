package com.mihalis.yandexbot.selenium;

import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.selenium.exceptions.NoDeliveryException;
import com.mihalis.yandexbot.selenium.exceptions.TooMuchAttemptsException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.Keys.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class BrowserPage extends Page {
    @Getter
    private Address userAddress;

    private Logger log;

    private WebElement yandexMaps, inputAddress;

    private boolean young = true;

    public BrowserPage(String yandexUrl, WebDriver browser) {
        super(yandexUrl, browser);

        log = LoggerFactory.getLogger(getClass().getSimpleName());
        log.info("Browser created!");

        init();
    }

    @Override
    protected void init() {
        removeUnnecessaryButton();

        clickNewAddressButton();
        clearOldAddress();
    }

    public void update(long userId, Address userAddress) {
        this.userAddress = userAddress;
        log = LoggerFactory.getLogger(String.valueOf(userId));
    }

    public void clickNewAddressButton() {
        Wait().until(elementToBeClickable(cssSelector("button[class='bzscopr c14xrn6c cow0qbn a71den4 m16coeem m1wd6zeg']"))).click();
        log.info("NewAddressButton clicked");
    }

    public void clickUpdateAddressButton() {
        Wait().until(elementToBeClickable(cssSelector("button[class='bzscopr c14xrn6c cow0qbn o134i4ad m16coeem m1wd6zeg']"))).click();
        log.info("UpdateAddressButton clicked");
    }

    @SneakyThrows
    public void clearOldAddress() {
        // while maps loads location, we just wait
        yandexMaps = Wait(60).until(elementToBeClickable(cssSelector("ymaps[class='ymaps-2-1-79-map']")));

        Thread.sleep(700);

        inputAddress = Wait().until(elementToBeClickable(cssSelector("input[class='i164506l']")));
        inputAddress.click();
        inputAddress.sendKeys(CONTROL + "a");
        inputAddress.sendKeys(DELETE);

        log.info("Delivery address cleared");
    }

    @SneakyThrows
    public void inputNewAddress() {
        log.error(userAddress.toString());
        inputWholeAddress(inputAddress, asList(userAddress.getTown(), userAddress.getStreet(), userAddress.getHouse()));
        waitForYmaps();

        while (true) {
            boolean listBoxVisible = (boolean) javaScript.executeScript(
                    "return document.getElementsByClassName('l1xltboq').length > 0;"
            );
            if (listBoxVisible) break;

            inputAddress.sendKeys(SPACE);
            Thread.sleep(500);
        }

        WebElement listBox = Wait().until(presenceOfElementLocated(cssSelector("ul[class='l1xltboq']")));
        if (addressesAreEquals(listBox.getText().toLowerCase())) {
            inputAddress.sendKeys(DOWN);
            inputAddress.sendKeys(ENTER);

            log.info("New address entered");
        } else {
            throw new TooMuchAttemptsException();
        }
    }

    public void applyNewAddress() {
        try {
            By okButtonScc = cssSelector("button[class='bzscopr f19ph74x c14xrn6c cow0qbn a71den4 m16coeem m1wd6zeg w3gf8dt']");
            Wait().until(presenceOfElementLocated(okButtonScc)).click();

            log.info("New address confirmed");
        } catch (Exception exception) {
            throw new NoDeliveryException(exception);
        }
    }

    public String getDeliveryCost() {
        if (!young) {
            refreshPage();
            removeUnnecessaryButton();
            log.info("refresh");
        } else {
            young = false;
        }

        By deliveryCostDiv = cssSelector("div[class='t1vrfrqt t18stym3 bw441np r88klks r1dbrdpx n10d4det l14lhr1r']");
        WebElement deliveryCost = Wait().until(visibilityOfElementLocated(deliveryCostDiv));

        log.info("Delivery cost received");
        return deliveryCost.getText();
    }

    public void cancel() {
        Wait().until(elementToBeClickable(cssSelector("button[class='c1vwcsci']"))).click();

        log.info("Popup canceled");
    }

    @SneakyThrows
    private void inputWholeAddress(WebElement inputAddress, List<String> addresses) {
        for (String address : addresses) {
            inputAddress.sendKeys(address);
            inputAddress.sendKeys(SPACE);

            waitForYmaps();
        }
    }

    @SneakyThrows
    private void waitForYmaps() {
        Wait().until(elementToBeClickable(yandexMaps));
        Thread.sleep(700);
    }

    private boolean addressesAreEquals(String text) {
        String[] split = text.split("\n");
        for (String l : split) {
            log.info(l + "         1111");
        }
        return true;
    }

    private void removeUnnecessaryButton() {
        // Later on this page will be placed button for changing delivery address.
        // And this button is equals button which shows catalog.
        // so remove unnecessary
        javaScript.executeScript("""
                    for (const button of document.getElementsByClassName("bzscopr c14xrn6c cow0qbn o134i4ad m16coeem m1wd6zeg")) {
                        if (button.hasAttribute("aria-haspopup")) {
                            button.remove();
                            break;
                        }
                    }
                """);
    }
}
