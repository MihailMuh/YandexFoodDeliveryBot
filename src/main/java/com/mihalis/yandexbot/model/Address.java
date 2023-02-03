package com.mihalis.yandexbot.model;

import lombok.*;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode
@Builder(access = AccessLevel.PRIVATE)
public class Address implements Serializable {
    private final String originalAddress;

    private final String formattedAddress;

    private final String town;
    private final String street;
    private final String house;

    public boolean notEmpty() {
        return originalAddress.length() > 0;
    }

    public static Address empty() {
        return new Address("", "", "", "", "");
    }

    public static Address of(String address) {
        String formatted = address.toLowerCase();
        val splitAddress = formatted.replace(" ", "").split(",");

        return Address
                .builder()
                .originalAddress(address)
                .formattedAddress(formatted)
                .town(splitAddress[0])
                .street(splitAddress[1])
                .house(splitAddress[2])
                .build();
    }
}
