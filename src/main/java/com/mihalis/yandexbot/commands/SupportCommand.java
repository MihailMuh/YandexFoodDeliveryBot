package com.mihalis.yandexbot.commands;

import com.mihalis.yandexbot.telegram.Bot;
import com.mihalis.yandexbot.telegram.PostMessage;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SupportCommand extends BaseCommand {
    private static final String supportMessage = "Если ты хочешь не только морально " +
            "поддержать разработчика: @mihalisM, то можешь скинуть по " +
            "номеру `+79087016312` какую-нибудь сумму 🙃";

    public SupportCommand() {
        super("support");
    }

    @SneakyThrows
    public void answer(Bot bot, Message message) {
        PostMessage sendMessage = new PostMessage(message);
        sendMessage.setText(supportMessage);
        sendMessage.setParseMode("Markdown");

        bot.executeAsync(sendMessage);
    }
}
