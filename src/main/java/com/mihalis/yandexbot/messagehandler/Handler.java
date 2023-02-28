package com.mihalis.yandexbot.messagehandler;

import com.mihalis.yandexbot.telegram.Parcel;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface Handler {
    boolean relevantCondition(Message message);

    void handleParcel(Parcel parcel);
}
