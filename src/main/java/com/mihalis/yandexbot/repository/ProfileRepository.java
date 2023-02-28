package com.mihalis.yandexbot.repository;

import com.mihalis.yandexbot.model.BrowserProfile;
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
    private String browserDataDir;

    @PostConstruct
    public void init() {
        String[] profiles = new File(browserDataDir + "/profiles").list();
        if (profiles == null || profiles.length == 0) {
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
                deleteDirectory(new File(browserDataDir + "/profiles", dirName));
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

    public synchronized BrowserProfile get() {
        if (busyProfilesName.isEmpty()) {
            String newProfileName = String.valueOf(maxProfileName.incrementAndGet());
            copyBaseProfile(newProfileName);

            return new BrowserProfile(newProfileName, true);
        }
        return new BrowserProfile(busyProfilesName.poll(), false);
    }

    @SneakyThrows
    private void copyBaseProfile(String newProfileName) {
        Runtime.getRuntime().exec("cp -r %1$s/base_profile %1$s/profiles/%2$s".formatted(browserDataDir, newProfileName));
    }
}
