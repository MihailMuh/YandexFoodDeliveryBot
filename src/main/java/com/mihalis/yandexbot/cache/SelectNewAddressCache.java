package com.mihalis.yandexbot.cache;

import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class SelectNewAddressCache {
    @Setter(value = AccessLevel.PRIVATE, onMethod_ = {@Autowired})
    private ValueOperations<String, Boolean> newAddressOperation;

    public void setActivatedNewAddressOperation(long userId, boolean activated) {
        newAddressOperation.set(String.valueOf(userId), activated);
    }

    public boolean isActiveNewAddressOperation(long userId) {
        return Boolean.TRUE.equals(newAddressOperation.get(String.valueOf(userId)));
    }
}
