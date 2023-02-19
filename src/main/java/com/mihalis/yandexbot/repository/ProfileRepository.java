package com.mihalis.yandexbot.repository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static org.apache.commons.io.FileUtils.deleteDirectory;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProfileRepository {
    private static final ConcurrentLinkedQueue<String> busyProfilesName = new ConcurrentLinkedQueue<>();

    private static final AtomicLong maxProfileName = new AtomicLong(0);

    private final ValueOperations<String, String> storage;

    @Value("${app.browser.data.dir}")
    private String browserProfilesDir;

    @PostConstruct
    public void init() {
        String[] profiles = new File(browserProfilesDir).list();
        if (profiles == null) {
            return;
        }

        deleteDeadProfiles(List.of(profiles));
        findMaxProfileName(stream(profiles).parallel());
    }

    @SneakyThrows
    private void deleteDeadProfiles(List<String> profiles) {
        List<String> userIds = storage.getOperations().keys("*").parallelStream().toList();
        List<String> profilesFromCache = storage.multiGet(userIds);

        int deleted = 0;
        for (String dirName : profiles) {
            if (!profilesFromCache.contains(dirName)) {
                deleteDirectory(new File(browserProfilesDir, dirName));
                deleted++;
            } else {
                busyProfilesName.add(dirName);
            }
        }

        log.info("Totally deleted {} unnecessary profiles", deleted);
    }

    private void findMaxProfileName(Stream<String> profilesStream) {
        maxProfileName.set(profilesStream.mapToLong(Long::parseLong).max().getAsLong());
    }

    public void set(long userId, String profileName) {
        storage.set(String.valueOf(userId), profileName);
    }

    public String get() {
        return busyProfilesName.isEmpty() ? String.valueOf(maxProfileName.incrementAndGet()) : busyProfilesName.poll();
    }
}
