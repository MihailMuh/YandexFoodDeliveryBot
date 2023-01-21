package com.mihalis.yandexbot.utils;

import java.util.regex.Pattern;

public final class StringUtils {
    public static int countMatches(String whereFind, String subString) {
        String copy = String.copyValueOf(whereFind.toCharArray());
        int count = 0;

        while (copy.contains(subString)) {
            copy = copy.replaceFirst(subString, "");
            count++;
        }

        return count;
    }

    public static boolean containsDigits(String whereFind) {
        return Pattern.compile("\\d").matcher(whereFind).find();
    }
}
