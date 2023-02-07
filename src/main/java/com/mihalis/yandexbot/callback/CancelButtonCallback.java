package com.mihalis.yandexbot.callback;

import com.mihalis.yandexbot.cache.AddressState;
import com.mihalis.yandexbot.telegram.Parcel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@AllArgsConstructor
public class CancelButtonCallback implements Callback {
    private final AddressState addressState;

    @Override
    public boolean relevantCondition(CallbackQuery callback) {
        return "cancel".equals(callback.getData());
    }

    @Override
    public void handleParcel(Parcel parcel) {
        parcel.answerAsync("Операция отменена");

        addressState.setActive(parcel.getUserId(), false);
    }
}
