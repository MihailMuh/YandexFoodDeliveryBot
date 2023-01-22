package com.mihalis.yandexbot.cache;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address {
    private final String originalAddress;

    private final String town;
    private final String street;
    private final String house;

    public static Address of(String address) {
        address = address.toLowerCase();
        val splitAddress = address.replace(" ", "").split(",");

        return new Address(address, splitAddress[0], splitAddress[1], splitAddress[2]);
    }
}
