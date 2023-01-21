package com.mihalis.yandexbot.commands;

import com.mihalis.yandexbot.beans.Telegram;
import com.mihalis.yandexbot.telegram.Bot;
import com.mihalis.yandexbot.telegram.PostMessage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {Telegram.class, SupportCommand.class})
class SupportCommandTests {
    @Autowired
    private SupportCommand supportCommand;

    @Autowired
    private Message message;

    @MockBean
    private Bot bot;

    @Test
    @SneakyThrows
    void shouldAnswer() {
        Mockito.when(bot.executeAsync(any(PostMessage.class))).thenReturn(any(CompletableFuture.class));

        supportCommand.answer(bot, message);

        Mockito.verify(bot, times(1)).executeAsync(any(PostMessage.class));
    }
}
