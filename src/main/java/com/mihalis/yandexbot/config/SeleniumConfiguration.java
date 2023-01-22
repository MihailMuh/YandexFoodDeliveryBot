package com.mihalis.yandexbot.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Log4j2
@Configuration
public class SeleniumConfiguration {
    @Bean(destroyMethod = "quit")
    public WebDriver getDriver() {
        WebDriverManager.firefoxdriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
//        options.setExperimentalOption("prefs", getPreferences());

        return new ChromeDriver(options) {
            @Override
            public void quit() {
                super.quit();

                log.info("Browser successfully disposed!");
            }
        };
    }

    private HashMap<String, HashMap> getPreferences() {
        HashMap<String, Integer> images = new HashMap<>();
        images.put("images", 2);

        HashMap<String, HashMap> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values", images);

        return prefs;
    }
}
