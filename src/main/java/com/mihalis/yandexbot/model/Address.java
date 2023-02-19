package com.mihalis.yandexbot.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Getter
@Builder
@ToString
@Jacksonized
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address implements Serializable {
    private final String originalAddress;

    private final String formattedAddress;

    private final String town;
    private final String street;
    private final String house;

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
