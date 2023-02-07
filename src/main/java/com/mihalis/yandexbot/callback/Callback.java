package com.mihalis.yandexbot.callback;

import com.mihalis.yandexbot.telegram.Parcel;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface Callback {
    boolean relevantCondition(CallbackQuery callback);

    void handleParcel(Parcel parcel);
}
