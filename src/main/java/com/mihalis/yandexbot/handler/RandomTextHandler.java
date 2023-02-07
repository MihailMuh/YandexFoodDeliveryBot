package com.mihalis.yandexbot.handler;

import com.mihalis.yandexbot.telegram.Parcel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class RandomTextHandler implements Handler {
    @Override
    public boolean relevantCondition(Message message) {
        return true; // can handle any message
    }

    @Override
    public void handleParcel(Parcel parcel) {
        String text = parcel.getText().toUpperCase();
        if (text.equals("АБОБА") || text.equals("ABOBA")) {
            parcel.answerAsync("❤️");
            return;
        }

        parcel.answerAsync("🤦");
    }
}
