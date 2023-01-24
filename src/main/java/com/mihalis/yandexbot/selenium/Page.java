package com.mihalis.yandexbot.selenium;

import lombok.SneakyThrows;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.time.Duration;

class Page {
    protected final WebDriver browser;

    protected Page(String url) {
        browser = generateBrowser();
        browser.get(url);
    }

    void refresh() {
        browser.navigate().refresh();
    }

    InputFile screenshot() {
        return new InputFile(((TakesScreenshot) browser).getScreenshotAs(OutputType.FILE));
    }

    void close() {
        try {
            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected final WebDriverWait Wait() {
        return new WebDriverWait(browser, Duration.ofSeconds(10));
    }

    @SneakyThrows
    private WebDriver generateBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized", "--no-sandbox", "--disable-dev-shm-usage");
        options.setHeadless(true);

        return new ChromeDriver(options);
    }
}
