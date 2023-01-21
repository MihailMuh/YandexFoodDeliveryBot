package com.mihalis.yandexbot.handlers;

import com.mihalis.yandexbot.beans.Telegram;
import com.mihalis.yandexbot.cache.SelectNewAddressCache;
import com.mihalis.yandexbot.handlers.CancelButtonHandler;
import com.mihalis.yandexbot.telegram.Bot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest(classes = {Telegram.class, CancelButtonHandler.class})
public class CancelButtonHandlerTests {
    @Autowired
    private CancelButtonHandler cancelButtonHandler;

    @Autowired
    private CallbackQuery callback;

    @MockBean
    private Bot bot;

    @MockBean
    private SelectNewAddressCache addressCache;

    @ParameterizedTest
    @ValueSource(strings = {
            "", "CANCEL", "Cancel"
    })
    void shouldReturn_whenInputNotCancel(String inputData) {
        Mockito.doNothing().when(bot).executeAsync(eq("Операция отменена"), any(Message.class));

        callback.setData(inputData);

        cancelButtonHandler.handleUpdate(bot, callback);

        Mockito.verify(bot, Mockito.never()).executeAsync("Операция отменена", callback.getMessage());
    }

    @Test
    void shouldAnswer_whenCorrectInput() {
        Mockito.doNothing().when(bot).executeAsync(eq("Операция отменена"), any(Message.class));
        Mockito.doNothing().when(addressCache).setActivatedNewAddressOperation(anyLong(), eq(false));

        callback.setData("cancel");

        cancelButtonHandler.handleUpdate(bot, callback);

        Mockito.verify(bot, times(1)).executeAsync("Операция отменена", callback.getMessage());
        Mockito.verify(addressCache, times(1)).
                setActivatedNewAddressOperation(callback.getMessage().getChatId(), false);
    }
}
