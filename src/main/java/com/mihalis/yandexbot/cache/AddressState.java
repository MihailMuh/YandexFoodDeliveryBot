package com.mihalis.yandexbot.cache;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AddressState {
    private final ValueOperations<Long, Boolean> addressStateStorage;

    public boolean isActive(long userId) {
        return Boolean.TRUE.equals(addressStateStorage.get(userId));
    }

    public void setActive(long userId, boolean activated) {
        addressStateStorage.set(userId, activated);
    }
}
