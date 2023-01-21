package com.mihalis.yandexbot.cache;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import static java.lang.String.join;

@Component
public class AddressCache {
    @Setter(value = AccessLevel.PRIVATE, onMethod_ = {@Autowired})
    private ValueOperations<Long, Address> addressesOperations;

    public void setAddress(long chatId, String address) {
        val splitAddress = address.split(", ");

        Address addressCache = new Address(address, join(", ",
                splitAddress[splitAddress.length - 2], splitAddress[splitAddress.length - 3]));

        addressesOperations.set(chatId, addressCache);
    }

    public Address getAddresses(long chatId) {
        Address address = addressesOperations.get(chatId);
        if (address == null) {
            return new Address("", "");
        }
        return address;
    }

    public record Address(String address, String street) {
    }
}
