package com.mihalis.yandexbot.config;

import com.mihalis.yandexbot.model.BrowserProfile;
import com.mihalis.yandexbot.repository.ProfileRepository;
import com.mihalis.yandexbot.selenium.BrowserPage;
import com.mihalis.yandexbot.selenium.PagePool;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
@RequiredArgsConstructor
public class SeleniumConfiguration {
    private final ProfileRepository profileRepository;

    @Value("${app.yandex.url}")
    private String yandexUrl;

    @Value("${app.browser.headless}")
    private boolean headless;

    @Value("${app.browser.data.dir}")
    private String browserDataDir;

    @Autowired
    @Bean(destroyMethod = "shutdown")
    public PagePool createPagePool(ExecutorService executorService, int pagePoolCapacity) {
        return new PagePool(executorService, pagePoolCapacity) {
            @Override
            protected BrowserPage page() {
                BrowserProfile profile = profileRepository.get();
                return new BrowserPage(yandexUrl, createBrowser(profile.getName()), profile);
            }
        };
    }

    @SneakyThrows
    private WebDriver createBrowser(String profileName) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--no-sandbox",
                "--start-maximized",
                "--user-data-dir=" + browserDataDir + "/profiles/" + profileName
        );
        if (headless) {
            options.addArguments(
                    "--headless"
            );
        }

        return new ChromeDriver(options);
    }
}
