package com.mihalis.yandexbot.handlers;

import com.mihalis.yandexbot.beans.Telegram;
import com.mihalis.yandexbot.telegram.Bot;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest(classes = {Telegram.class, RandomTextHandler.class})
class RandomTextHandlerTests {
    @Autowired
    private RandomTextHandler randomTextHandler;

    @Autowired
    private Message message;

    @MockBean
    private Bot bot;

    @ParameterizedTest
    @ValueSource(strings = {"АБОБА", "абоба", "ABOBA", "aboba", "АбОбА", "aBoBa"})
    void shouldSendHeartSmile(String userInput) {
        Mockito.doNothing().when(bot).executeAsync(eq("❤️"), any(Message.class));

        message.setText(userInput);
        randomTextHandler.handleUpdate(bot, message);

        Mockito.verify(bot, times(1)).executeAsync("❤️", message);
    }

    @ParameterizedTest
    @MethodSource("differentStrings")
    void shouldFacepalm_whenUserSentTextMessage(String userInput) {
        Mockito.doNothing().when(bot).executeAsync(eq("🤦"), any(Message.class));

        message.setText(userInput);
        randomTextHandler.handleUpdate(bot, message);

        Mockito.verify(bot).executeAsync("🤦", message);
    }

    private static Stream<String> differentStrings() {
        return Stream.of("Q".repeat(4096), "❤️", "Hello, World!", "777");
    }
}
