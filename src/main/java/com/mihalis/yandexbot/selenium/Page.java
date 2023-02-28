package com.mihalis.yandexbot.selenium;

import com.mihalis.yandexbot.model.BrowserProfile;
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

    private final BrowserProfile profile;

    public Page(String url, WebDriver browser, BrowserProfile profile) {
        this.browser = browser;
        this.profile = profile;
        this.javaScript = (JavascriptExecutor) browser;

        log = LoggerFactory.getLogger(getClass().getSimpleName());
        log.info("Browser created!");

        browser.get(url);
    }

    public void close() {
        try {
            browser.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File screenshot() {
        return ((TakesScreenshot) browser).getScreenshotAs(OutputType.FILE);
    }

    public boolean profileIsNew() {
        return profile.isNew();
    }

    public String getProfileName() {
        return profile.getName();
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
}
