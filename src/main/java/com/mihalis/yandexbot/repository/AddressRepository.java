package com.mihalis.yandexbot.repository;

import com.mihalis.yandexbot.model.Address;
import lombok.val;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AddressRepository {
    private final ValueOperations<String, Address> addressStorage;

    private final RedisOperations<String, Address> redisOperations;

    public AddressRepository(ValueOperations<String, Address> addressStorage) {
        this.addressStorage = addressStorage;
        redisOperations = addressStorage.getOperations();
    }

    public List<Pair<Long, Address>> items() {
        val userIds = redisOperations.keys("*").parallelStream().toList();
        val items = new ArrayList<Pair<Long, Address>>();
        val addresses = addressStorage.multiGet(userIds);

        for (int i = 0; i < userIds.size(); i++) {
            items.add(Pair.of(Long.parseLong(userIds.get(i)), addresses.get(i)));
        }

        return items;
    }

    public void setAddress(long chatId, Address address) {
        addressStorage.set(String.valueOf(chatId), address);
    }

    public boolean hasAddress(long chatId) {
        return Boolean.TRUE.equals(redisOperations.hasKey(String.valueOf(chatId)));
    }

    public void deleteAddress(long chatId) {
        redisOperations.delete(String.valueOf(chatId));
    }

    public Address getAddress(long chatId) {
        Address address = addressStorage.get(String.valueOf(chatId));
        if (address == null) {
            return Address.empty();
        }
        return address;
    }
}
