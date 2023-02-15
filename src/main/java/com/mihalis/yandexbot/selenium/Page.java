package com.mihalis.yandexbot.selenium;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

abstract class Page {
    protected final WebDriver browser;

    protected final JavascriptExecutor javaScript;

    public Page(String url, WebDriver browser) {
        this.browser = browser;
        this.javaScript = (JavascriptExecutor) browser;

        browser.get(url);
    }

    public void close() {
        try {
            browser.close();
            browser.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File screenshot() {
        return ((TakesScreenshot) browser).getScreenshotAs(OutputType.FILE);
    }

    protected abstract void init();

    protected void refreshPage() {
        browser.navigate().refresh();
    }

    void reset() {
        browser.manage().deleteAllCookies();
        refreshPage();
        init();
    }

    protected final WebDriverWait Wait() {
        return Wait(15);
    }

    protected WebDriverWait Wait(int seconds) {
        return new WebDriverWait(browser, Duration.ofSeconds(seconds));
    }
}
