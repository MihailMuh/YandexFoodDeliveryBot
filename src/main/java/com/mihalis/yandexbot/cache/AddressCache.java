package com.mihalis.yandexbot.cache;

import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class AddressCache {
    @Setter(value = AccessLevel.PRIVATE, onMethod_ = {@Autowired})
    private ValueOperations<Long, Address> addressesOperations;

    public void setAddress(long chatId, Address address) {
        addressesOperations.set(chatId, address);
    }

    public Address getAddress(long chatId) {
        Address address = addressesOperations.get(chatId);
        if (address == null) {
            return Address.of(", , ");
        }
        return address;
    }
}
