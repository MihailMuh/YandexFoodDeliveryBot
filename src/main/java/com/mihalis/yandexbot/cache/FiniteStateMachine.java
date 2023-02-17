package com.mihalis.yandexbot.cache;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FiniteStateMachine {
    private final HashOperations<String, String, Object> finiteStateMachineStorage;

    public void setValue(long userId, String key, Object value) {
        finiteStateMachineStorage.put(str(userId), key, value);
    }

    public Object getValue(long userId, String key) {
        return finiteStateMachineStorage.get(str(userId), key);
    }

    public boolean hasState(long userId) {
        return Boolean.TRUE.equals(finiteStateMachineStorage.getOperations().hasKey(str(userId)));
    }

    public void delete(long userId) {
        finiteStateMachineStorage.getOperations().delete(str(userId));
    }

    private static String str(long id) {
        return String.valueOf(id);
    }
}
