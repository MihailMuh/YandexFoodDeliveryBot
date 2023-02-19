package com.mihalis.yandexbot.utils;

public interface Wrapper<R, A> {
    R accept(A arg);
}
