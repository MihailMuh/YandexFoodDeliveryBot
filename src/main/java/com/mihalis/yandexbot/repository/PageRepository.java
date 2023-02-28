package com.mihalis.yandexbot.repository;

import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.model.DeliveryData;
import com.mihalis.yandexbot.selenium.BrowserPage;
import com.mihalis.yandexbot.selenium.PagePool;
import com.mihalis.yandexbot.selenium.exceptions.NoDeliveryException;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

@Slf4j
@Repository
public class PageRepository {
    private static final ConcurrentHashMap<Long, BrowserPage> pages = new ConcurrentHashMap<>();

    private final ExecutorService executorService;

    private final PagePool pagePool;

    @SneakyThrows
    public PageRepository(AddressRepository addressRepository, ExecutorService executorService, PagePool pagePool) {
        this.executorService = executorService;
        this.pagePool = pagePool;

        List<Pair<Long, Address>> addresses = addressRepository.items();
        CountDownLatch countDownLatch = new CountDownLatch(addresses.size());

        log.info("Initializing " + addresses.size() + " web pages");

        for (Pair<Long, Address> pair : addresses) {
            executorService.execute(() -> {
                createPage(pair.getFirst(), pair.getSecond());

                countDownLatch.countDown();
            });
        }

        countDownLatch.await();
    }

    public void createPage(long userId, Address address) {
        BrowserPage page = pagePool.obtain(userId, address);
        pages.put(userId, page);

        if (page.profileIsNew()) {
            page.inputNewAddress();
        }
    }

    public void deletePage(long userId) {
        if (pages.containsKey(userId)) {
            pagePool.free(pages.remove(userId));

            log.info("Old page deleted");
        }
    }

    public String getDeliveryCost(long userId, int addressRowIndex) throws NoDeliveryException {
        BrowserPage page = pages.get(userId);
        page.applyAddress(addressRowIndex);

        return page.getDeliveryCost();
    }

    public List<String> getDeliveryAddresses(long userId) {
        return pages.get(userId).getDeliveryAddresses();
    }

    public void iterateDeliveryCosts(Consumer<DeliveryData> costConsumer) {
        List<Map.Entry<Long, BrowserPage>> pagesList = pages.entrySet().parallelStream().toList();

        for (Map.Entry<Long, BrowserPage> entry : pagesList) {
            executorService.execute(() -> costConsumer.accept(createDeliveryData(entry)));
        }
    }

    public InputFile getScreenshot(long userId) {
        return new InputFile(pages.get(userId).screenshot());
    }

    public String getBrowserProfileName(long userId) {
        return pages.get(userId).getProfileName();
    }

    private DeliveryData createDeliveryData(Map.Entry<Long, BrowserPage> entry) {
        return DeliveryData
                .builder()
                .address(entry.getValue().getUserAddress().getOriginalAddress())
                .userId(entry.getKey())
                .deliveryCost(entry.getValue().getDeliveryCost())
                .build();
    }

    @PreDestroy
    private void shutdown() {
        val pagesList = pages.values();

        for (BrowserPage page : pagesList) {
            page.close();
        }

        log.info("Totally " + pagesList.size() + " pages disposed");
    }
}
