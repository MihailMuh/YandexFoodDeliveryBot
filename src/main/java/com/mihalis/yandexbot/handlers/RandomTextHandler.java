package com.mihalis.yandexbot.handlers;

import com.mihalis.yandexbot.telegram.Bot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class RandomTextHandler {
    public void handleUpdate(Bot bot, Message message) {
        String text = message.getText().toUpperCase();
        if (text.equals("АБОБА") || text.equals("ABOBA")) {
            bot.executeAsync("❤️", message);
            return;
        }

        bot.executeAsync("🤦", message);
    }
}
