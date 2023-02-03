package com.mihalis.yandexbot.selenium;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.time.Duration;

abstract class Page {
    private final String url;

    protected final WebDriver browser;

    public Page(String url, WebDriver browser) {
        this.url = url;
        this.browser = browser;

        reset();
    }

    public InputFile screenshot() {
        return new InputFile(((TakesScreenshot) browser).getScreenshotAs(OutputType.FILE));
    }

    public void close() {
        try {
            browser.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void reset() {
        browser.manage().deleteAllCookies();
        browser.get(url);
    }

    protected final WebDriverWait Wait() {
        return Wait(15);
    }

    protected WebDriverWait Wait(int seconds) {
        return new WebDriverWait(browser, Duration.ofSeconds(seconds));
    }
}
