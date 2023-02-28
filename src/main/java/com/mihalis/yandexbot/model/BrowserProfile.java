package com.mihalis.yandexbot.model;

import lombok.*;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class BrowserProfile {
    @Getter
    private final String name;

    private final boolean isNew;

    public boolean isNew() {
        return isNew;
    }
}
