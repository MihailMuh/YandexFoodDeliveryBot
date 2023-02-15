package com.mihalis.yandexbot.selenium;

import com.mihalis.yandexbot.model.Address;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
public abstract class PagePool {
    private final ConcurrentLinkedQueue<BrowserPage> queue = new ConcurrentLinkedQueue<>();

    private final ExecutorService executorService;

    @SneakyThrows
    public PagePool(ExecutorService executorService, int pagePoolCapacity) {
        this.executorService = executorService;

        log.info("Creating " + pagePoolCapacity + " browsers...");

        CountDownLatch countDownLatch = new CountDownLatch(pagePoolCapacity);

        for (int i = 0; i < pagePoolCapacity; i++) {
            executorService.execute(() -> {
                generatePage();
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();
    }

    public BrowserPage obtain(long userId, Address userAddress) {
        if (queue.size() <= 1) {
            int length = 3 - queue.size();
            for (int i = 0; i < length; i++) {
                executorService.execute(this::generatePage);
            }
        }

        BrowserPage page = queue.poll();
        page.update(userId, userAddress);
        return page;
    }

    public void free(BrowserPage browserPage) {
        executorService.execute(() -> {
            browserPage.reset();
            queue.add(browserPage);
        });
    }

    protected abstract BrowserPage page();

    private void generatePage() {
        queue.add(page());
    }

    public void shutdown() {
        for (BrowserPage page : queue) {
            page.close();
        }
        log.info("Totally " + queue.size() + " pages disposed");
    }
}
