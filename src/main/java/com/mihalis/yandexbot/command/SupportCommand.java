package com.mihalis.yandexbot.command;

import com.mihalis.yandexbot.telegram.Parcel;
import org.springframework.stereotype.Component;

import static com.mihalis.yandexbot.data.StringMessages.supportMe;
import static org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN;

@Component
public class SupportCommand extends Command {
    public SupportCommand() {
        super("support");
    }

    public void answer(Parcel parcel) {
        parcel.answerAsync(supportMe, MARKDOWN);
    }
}
