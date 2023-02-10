package com.mihalis.yandexbot.command;

import com.mihalis.yandexbot.telegram.Parcel;
import org.springframework.stereotype.Component;

import static com.mihalis.yandexbot.data.StringMessages.greeting;

@Component
public class StartCommand extends Command {
    public StartCommand() {
        super("start");
    }

    @Override
    public void answer(Parcel parcel) {
        parcel.answerAsync(greeting);
    }
}
