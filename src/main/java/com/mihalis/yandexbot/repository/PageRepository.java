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
                // here address is valid, but there's a chance, that function crashes
                // if it happens - just repeat
                while (true) {
                    try {
                        createPage(pair.getFirst(), pair.getSecond());
                        countDownLatch.countDown();
                        return;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        deletePage(pair.getFirst());
                    }
                }
            });
        }

        countDownLatch.await();
    }

    public void createPage(long userId, Address address) {
        BrowserPage page;
        if (pages.containsKey(userId)) {
            page = pages.get(userId);

            page.update(userId, address);
            page.clickNewAddressButton();
            page.clearOldAddress();
        } else {
            page = pagePool.obtain(userId, address);
            pages.put(userId, page);
        }
        page.inputNewAddress();
    }

    public void deletePage(long id) {
        if (pages.containsKey(id)) {
            pagePool.free(pages.remove(id));

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
