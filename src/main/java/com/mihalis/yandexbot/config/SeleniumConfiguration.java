package com.mihalis.yandexbot.config;

import com.mihalis.yandexbot.selenium.BrowserPage;
import com.mihalis.yandexbot.selenium.PagePool;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
class SeleniumConfiguration {
    @Value("${app.yandex.url}")
    private String yandexUrl;

    @Value("${app.browser.headless}")
    private boolean headless;

    @Autowired
    @Bean(destroyMethod = "shutdown")
    public PagePool createPagePool(ExecutorService executorService, int pagePoolCapacity) {
        return new PagePool(executorService, pagePoolCapacity) {
            @Override
            protected BrowserPage page() {
                return createPage();
            }
        };
    }

    private BrowserPage createPage() {
        return new BrowserPage(yandexUrl, createBrowser());
    }

    private WebDriver createBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized", "--disable-dev-shm-usage");
        options.setHeadless(headless);

        return new ChromeDriver(options);
    }
}
