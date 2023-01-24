package com.mihalis.yandexbot.selenium;

import com.mihalis.yandexbot.cache.Address;
import com.mihalis.yandexbot.selenium.exceptions.AttemptException;
import com.mihalis.yandexbot.selenium.exceptions.NoDeliveryException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.Keys.DOWN;
import static org.openqa.selenium.Keys.ENTER;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

class BrowserPage extends Page {
    @Getter(AccessLevel.PACKAGE)
    private final Address userAddress;

    private final Logger log;

    @FindBy(css = "div[class^='shown']")
    private WebElement setAddressButton;

    private boolean young = true;

    BrowserPage(String userId, Address userAddress) {
        super("https://15min.market.yandex.ru/54");
        this.userAddress = userAddress;

        log = LogManager.getLogger(userId);
        log.info("Browser created!");

        PageFactory.initElements(browser, this);
    }

    void clickSetAddressBtn() {
        setAddressButton.click();
        log.info("SetAddressButton clicked");
    }

    void clearDeliveryAddress() {
        // while maps loads location, we just wait
        Wait().until(elementToBeClickable(cssSelector("ymaps[class='ymaps-2-1-79-map']")));

        WebElement clearAddressButton = Wait().until(elementToBeClickable(cssSelector("button[class='c12fmzph']")));
        clearAddressButton.click();

        log.info("Delivery address cleared");
    }

    @SneakyThrows
    void inputNewAddress() {
        WebElement inputField = browser.findElement(cssSelector("input[class='i164506l']"));
        inputField.sendKeys(userAddress.getOriginalAddress());

        WebElement listBox = Wait().until(visibilityOfElementLocated(cssSelector("ul[class='l1xltboq']")));

        // 5 attempts to detect address
        for (int i = 0; i < 5; i++) {
            // if slooooow map clears my address and insert its address from satellite
            if (!inputField.getAttribute("value").contains(userAddress.getOriginalAddress())) {
                log.info("Slow map break my plans... Clearing...");
                clearDeliveryAddress();
                inputNewAddress();
                return;
            }

            if (containsRightAddress(listBox.getText().toLowerCase())) {
                Thread.sleep(500);

                inputField.sendKeys(DOWN);
                inputField.sendKeys(ENTER);

                log.info("New address entered");
                return;
            }

            log.info(i + " attempt");
            inputField.sendKeys(" "); // this move refreshes list box (ha, yandex logic)

            Thread.sleep(500);
            Wait().until(elementToBeClickable(cssSelector("ymaps[class='ymaps-2-1-79-map']")));
        }

        throw new AttemptException();
    }

    void applyNewAddress() {
        try {
            By okButtonScc = cssSelector("button[class='bzscopr f19ph74x c14xrn6c cow0qbn a71den4 m16coeem m1wd6zeg w3gf8dt']");
            WebElement okButton = Wait().until(elementToBeClickable(okButtonScc));
            okButton.click();

            log.info("New address confirmed");
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new NoDeliveryException();
        }
    }

    String getDeliveryCost() {
        if (!young) {
            refresh();
            log.info("refresh");
            young = false;
        }

        By okButtonScc = cssSelector("div[class='t1vrfrqt t18stym3 bw441np r88klks r1dbrdpx n10d4det l14lhr1r']");
        WebElement deliveryCost = Wait().until(elementToBeClickable(okButtonScc));

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
}
