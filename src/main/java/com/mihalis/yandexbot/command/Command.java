package com.mihalis.yandexbot.command;

import com.mihalis.yandexbot.telegram.Parcel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
public abstract class Command {
    @Getter
    private final String commandIdentifier;

    protected Command(String commandIdentifier) {
        if (!commandIdentifier.startsWith("/")) {
            commandIdentifier = "/" + commandIdentifier;
        }

        this.commandIdentifier = commandIdentifier;
    }

    public void processMessage(AbsSender sender, Message message) {
        answer(new Parcel(sender, message));
        log.info("@" + message.getChat().getUserName() + ": " + commandIdentifier + " - successfully");
    }

    protected abstract void answer(Parcel parcel);
}
