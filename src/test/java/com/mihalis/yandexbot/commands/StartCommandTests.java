package com.mihalis.yandexbot.commands;

import com.mihalis.yandexbot.beans.Telegram;
import com.mihalis.yandexbot.telegram.Bot;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {Telegram.class, StartCommand.class})
class StartCommandTests {
    @Autowired
    private StartCommand startCommand;

    @Autowired
    private Message message;

    @MockBean
    private Bot bot;

    @SneakyThrows
    @Test
    void shouldAnswer() {
        String greeting = getStartCommandGreeting();

        Mockito.doNothing().when(bot).executeAsync(eq(greeting), any(Message.class));

        startCommand.answer(bot, message);

        Mockito.verify(bot, times(1)).executeAsync(greeting, message);
    }

    @SneakyThrows
    private String getStartCommandGreeting() {
        Field greetingField = StartCommand.class.getDeclaredField("greeting");
        greetingField.setAccessible(true);
        return greetingField.get(null).toString();
    }
}
