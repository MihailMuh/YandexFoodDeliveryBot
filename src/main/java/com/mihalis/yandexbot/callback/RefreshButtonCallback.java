package com.mihalis.yandexbot.callback;

import com.mihalis.yandexbot.cache.FiniteStateMachine;
import com.mihalis.yandexbot.messagehandler.ChooseDeliveryAddressHandler;
import com.mihalis.yandexbot.model.Address;
import com.mihalis.yandexbot.telegram.Parcel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class RefreshButtonCallback implements Callback {
    private final FiniteStateMachine finiteStateMachine;

    private final ChooseDeliveryAddressHandler chooseDeliveryAddressHandler;

    @Override
    public boolean relevantCondition(CallbackQuery callback) {
        return "refresh".equals(callback.getData()) &&
                finiteStateMachine.hasState(callback.getMessage().getChatId());
    }

    @Override
    public void handleParcel(Parcel parcel) {
        Address userAddress = (Address) finiteStateMachine.getValue(parcel.getUserId(), "userAddress");
        chooseDeliveryAddressHandler.answerWithKeyboardFromBrowser(parcel, userAddress);

        parcel.answerAsync("Сверяю...");
    }
}
