package com.mihalis.yandexbot.selenium;

import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.selenium.exceptions.NoDeliveryException;
import com.mihalis.yandexbot.selenium.exceptions.TooMuchAttemptsException;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.Keys.DOWN;
import static org.openqa.selenium.Keys.ENTER;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class BrowserPage extends Page {
    @Getter
    private Address userAddress;

    private Logger log;

    private WebElement yandexMaps;

    private boolean young = true;

    public BrowserPage(String yandexUrl, WebDriver browser) {
        super(yandexUrl, browser);

        log = LoggerFactory.getLogger(getClass().getSimpleName());
        log.info("Browser created!");

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

    public void clearOldAddress() {
        // while maps loads location, we just wait
        yandexMaps = Wait(60).until(elementToBeClickable(cssSelector("ymaps[class='ymaps-2-1-79-map']")));

        Wait().until(visibilityOfElementLocated(cssSelector("button[class='c12fmzph']"))).click();
        log.info("Delivery address cleared");
    }

    @SneakyThrows
    public void inputNewAddress() {
        WebElement inputAddress = Wait().until(elementToBeClickable(cssSelector("input[class='i164506l']")));
        inputAddress.sendKeys(userAddress.getFormattedAddress());

        // 5 attempts to detect address
        for (int i = 0; i < 5; i++) {
            // if slooooow map clears my address and insert its address from satellite
            if (!inputAddress.getAttribute("value").contains(userAddress.getFormattedAddress())) {
                log.info("Slow map break my plans... Clearing...");
                clearOldAddress();
                inputNewAddress();
                return;
            }

            WebElement listBox = Wait().until(visibilityOfElementLocated(cssSelector("ul[class='l1xltboq']")));
            if (containsRightAddress(listBox.getText().toLowerCase())) {
                inputAddress.sendKeys(DOWN);
                inputAddress.sendKeys(ENTER);

                log.info("New address entered");
                return;
            }

            log.info(i + " attempt");
            inputAddress.sendKeys(" "); // this move refreshes list box (ha, yandex logic)

            Wait().until(elementToBeClickable(yandexMaps));
            Thread.sleep(300);
        }

        throw new TooMuchAttemptsException();
    }

    public void applyNewAddress() {
        try {
            By okButtonScc = cssSelector("button[class='bzscopr f19ph74x c14xrn6c cow0qbn a71den4 m16coeem m1wd6zeg w3gf8dt']");
            Wait().until(elementToBeClickable(okButtonScc)).click();

            log.info("New address confirmed");
        } catch (Exception exception) {
            throw new NoDeliveryException(exception);
        }
    }

    public String getDeliveryCost() {
        if (!young) {
            browser.navigate().refresh();
            log.info("refresh");
        } else {
            young = false;
        }

        By deliveryCostDiv = cssSelector("div[class='t1vrfrqt t18stym3 bw441np r88klks r1dbrdpx n10d4det l14lhr1r']");
        WebElement deliveryCost = Wait().until(elementToBeClickable(deliveryCostDiv));

        log.info("Delivery cost received");
        return deliveryCost.getText();
    }

    private boolean containsRightAddress(String listBoxText) {
        val split = listBoxText.replace(" ", "").split("\n");
        val streetHouse = split[0].split(",");
        String town = split[1];

        // if in streetHouse only street (slow ymaps)
        if (streetHouse.length == 1) return false;

        return streetHouse[0].contains(userAddress.getStreet()) &&
                streetHouse[1].contains(userAddress.getHouse()) &&
                town.contains(userAddress.getTown());
    }

    private void removeUnnecessaryButton() {
        // Later on this page will be placed button for changing delivery address.
        // And this button is equals button which shows catalog.
        // so remove unnecessary
        ((JavascriptExecutor) browser).executeScript("""
                for (const button of document.getElementsByClassName("bzscopr c14xrn6c cow0qbn o134i4ad m16coeem m1wd6zeg")) {
                    if (button.hasAttribute("aria-haspopup")) {
                        button.remove();
                        break;
                    }
                }
                """);
    }
}
