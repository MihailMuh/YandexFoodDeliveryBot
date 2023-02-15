package com.mihalis.yandexbot.cache;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@AllArgsConstructor
public class FiniteStateMachine {
    private final ValueOperations<Long, HashMap> finiteStateMachineStorage;

    public void setValue(long userId, String key, Object value) {
        HashMap<String, Object> data = getDict(userId);
        data.put(key, value);

        finiteStateMachineStorage.set(userId, data);
    }

    public Object getValue(long userId, String key) {
        return getDict(userId).get(key);
    }

    public boolean hasState(long userId) {
        return Boolean.TRUE.equals(finiteStateMachineStorage.getOperations().hasKey(userId));
    }

    public void delete(long userId) {
        finiteStateMachineStorage.getOperations().delete(userId);
    }

    private HashMap<String, Object> getDict(long userId) {
        if (Boolean.FALSE.equals(finiteStateMachineStorage.getOperations().hasKey(userId))) {
            return new HashMap<>();
        }
        return finiteStateMachineStorage.get(userId);
    }
}
