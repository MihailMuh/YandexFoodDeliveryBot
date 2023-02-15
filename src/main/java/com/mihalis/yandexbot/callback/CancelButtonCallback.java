package com.mihalis.yandexbot.callback;

import com.mihalis.yandexbot.cache.FiniteStateMachine;
import com.mihalis.yandexbot.service.YandexFoodService;
import com.mihalis.yandexbot.telegram.Parcel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@AllArgsConstructor
public class CancelButtonCallback implements Callback {
    private final FiniteStateMachine finiteStateMachine;

    private final YandexFoodService yandexFoodService;

    @Override
    public boolean relevantCondition(CallbackQuery callback) {
        return "cancel".equals(callback.getData()) && finiteStateMachine.hasState(callback.getMessage().getChatId());
    }

    @Override
    public void handleParcel(Parcel parcel) {
        parcel.answerAsync("Операция отменена");

        finiteStateMachine.delete(parcel.getUserId());
        yandexFoodService.deleteAddress(parcel.getUserId()); // delete unnecessary page
    }
}
