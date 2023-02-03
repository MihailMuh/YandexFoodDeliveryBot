package com.mihalis.yandexbot.commands;

import com.mihalis.yandexbot.telegram.Bot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
@Getter
@AllArgsConstructor
public abstract class BaseCommand implements IBotCommand {
    private String commandIdentifier;

    @Override
    public String getDescription() {
        return null;
    }

    public abstract void answer(Bot bot, Message message);

    @Override
    public void processMessage(AbsSender sender, Message message, String... arguments) {
        answer((Bot) sender, message);
        log.info("@" + message.getChat().getUserName() + ": /" + commandIdentifier + " - successfully");
    }
}
