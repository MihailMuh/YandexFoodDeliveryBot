package com.mihalis.yandexbot.selenium;

import lombok.Getter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;

abstract class Page {
    protected final WebDriver browser;

    protected final JavascriptExecutor javaScript;

    protected Logger log;

    @Getter
    private final String profileName;

    protected boolean clearProfile;

    public Page(String url, WebDriver browser, String profileName) {
        this.browser = browser;
        this.profileName = profileName;
        this.javaScript = (JavascriptExecutor) browser;

        log = LoggerFactory.getLogger(getClass().getSimpleName());
        log.info("Browser created!");

        browser.get(url);

        checkProfileIsClear();
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

    public boolean hasClearProfile() {
        return clearProfile;
    }

    void reset() {
        browser.manage().deleteAllCookies();
        refreshPage();
        init();
    }

    protected abstract void init();

    protected void refreshPage() {
        browser.navigate().refresh();
    }

    protected final WebDriverWait Wait() {
        return Wait(15);
    }

    protected WebDriverWait Wait(int seconds) {
        return new WebDriverWait(browser, Duration.ofSeconds(seconds));
    }

    protected void checkProfileIsClear() {
        // there is no on page button "Enter delivery address"
        clearProfile = (boolean) javaScript.executeScript("""
                        return document.querySelector("div[class='shown cc9u8rz']") !== null;
                """);

        if (!clearProfile) {
            log.info("User profile restored");
        }
    }
}
