package com.mihalis.yandexbot.utils;

import static com.mihalis.yandexbot.utils.StringUtils.containsDigits;
import static com.mihalis.yandexbot.utils.StringUtils.countMatches;

public final class AddressValidator {
    public static boolean isValid(String address) {
        return countMatches(address, ",") < 2
                || !containsDigits(address)
                || address.charAt(0) == ','
                || address.charAt(address.length() - 1) == ','
                || address.contains(",,");
    }
}
