package com.mihalis.yandexbot.command;

import com.mihalis.yandexbot.telegram.Parcel;
import org.springframework.stereotype.Component;

import static org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN;

@Component
public class SupportCommand extends Command {
    private static final String supportMessage = "Если ты хочешь не только морально " +
            "поддержать разработчика: @mihalisM, то можешь скинуть по " +
            "номеру `+79087016312` какую-нибудь сумму 🙃";

    public SupportCommand() {
        super("support");
    }

    public void answer(Parcel parcel) {
        parcel.answerAsync(supportMessage, MARKDOWN);
    }
}
