package com.mihalis.yandexbot.selenium;

import com.mihalis.yandexbot.cache.Address;
import com.mihalis.yandexbot.cache.AddressCache;
import com.mihalis.yandexbot.utils.DeliveryData;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Log4j2
@Repository
class PageRepository {
    private final ConcurrentHashMap<String, BrowserPage> pages = new ConcurrentHashMap<>();

    private final ExecutorService executorService;

    PageRepository(AddressCache addressCache, ExecutorService executorService) {
        this.executorService = executorService;

        List<Pair<String, Address>> items = addressCache.items();

        if (items.size() > 0) {
            log.info("Generating browsers...");
        }

        for (val pair : items) {
            executorService.execute(() -> {
                // here address is valid, but there's a chance, that function crashes
                // if it happens - just repeat
                while (true) {
                    try {
                        setNewPage(pair.getFirst(), pair.getSecond());
                        return;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            });
        }
    }

    void setNewPage(long userId, Address address) {
        setNewPage(String.valueOf(userId), address);
    }

    void setNewPage(String id, Address address) {
        deletePage(id);

        BrowserPage page = new BrowserPage(id, address);
        pages.put(id, page);

        page.clickSetAddressBtn();
        page.clearDeliveryAddress();
        page.inputNewAddress();
            applyNewAddress(page);
    }

    void deletePage(String id) {
        if (pages.containsKey(id)) {
            pages.get(id).close();
            pages.remove(id);

            log.info("Old page deleted");
        }
    }

    void deletePage(long userId) {
        deletePage(String.valueOf(userId));
    }

    @SneakyThrows
    List<DeliveryData> getAllDeliveryCosts() {
        val costs = new ArrayList<DeliveryData>(pages.size());
        val webPages = pages.entrySet().parallelStream().toList();
        val latch = new CountDownLatch(pages.size());

        for (int i = 0; i < webPages.size(); i++) {
            val finalI = i;

            executorService.execute(() -> {
                costs.add(getDataFromPages(webPages, finalI));
                latch.countDown();
            });
        }

        latch.await();
        return costs;
    }

    String getDeliveryCost(long userId) {
        return page(userId).getDeliveryCost();
    }

    InputFile takeScreenshot(long userId) {
        return page(userId).screenshot();
    }

    private void applyNewAddress(BrowserPage browserPage) {
        browserPage.applyNewAddress();
    }

    private BrowserPage page(long userId) {
        return pages.get(String.valueOf(userId));
    }

    private DeliveryData getDataFromPages(List<Map.Entry<String, BrowserPage>> pages, int index) {
        Map.Entry<String, BrowserPage> entry = pages.get(index);

        return new DeliveryData(
                Long.valueOf(entry.getKey()),
                entry.getValue().getUserAddress().getOriginalAddress(),
                entry.getValue().getDeliveryCost()
        );
    }

    @PreDestroy
    private void onShutdown() {
        for (BrowserPage page : pages.values()) {
            page.close();
        }
        log.info("Browsers successfully disposed");
    }
}
