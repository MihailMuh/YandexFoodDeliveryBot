package com.mihalis.yandexbot.cache;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class AddressCache {
    private final ValueOperations<String, Address> addressesOperations;

    private final RedisOperations<String, Address> redisOperations;

    public AddressCache(ValueOperations<String, Address> addressesOperations) {
        this.addressesOperations = addressesOperations;
        redisOperations = addressesOperations.getOperations();
    }

    public List<Pair<String, Address>> items() {
        val userIds = redisOperations.keys("*").parallelStream().toList();
        val items = new ArrayList<Pair<String, Address>>();
        val addresses = addressesOperations.multiGet(userIds);

        for (int i = 0; i < userIds.size(); i++) {
            items.add(Pair.of(userIds.get(i), addresses.get(i)));
        }

        return items;
    }

    public void setAddress(long chatId, Address address) {
        addressesOperations.set(String.valueOf(chatId), address);
    }

    public void deleteAddress(long chatId) {
        redisOperations.delete(String.valueOf(chatId));
    }

    public Address getAddress(long chatId) {
        Address address = addressesOperations.get(String.valueOf(chatId));
        if (address == null) {
            return Address.empty();
        }
        return address;
    }
}
