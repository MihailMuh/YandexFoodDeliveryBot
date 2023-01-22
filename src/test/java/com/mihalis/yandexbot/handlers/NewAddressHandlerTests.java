package com.mihalis.yandexbot.handlers;

import com.mihalis.yandexbot.beans.Telegram;
import com.mihalis.yandexbot.cache.SelectNewAddressCache;
import com.mihalis.yandexbot.telegram.Bot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest(classes = {Telegram.class, NewAddressHandler.class})
public class NewAddressHandlerTests {
    @Autowired
    private NewAddressHandler newAddressHandler;

    @Autowired
    private Message message;

    @MockBean
    private Bot bot;

    @MockBean
    private SelectNewAddressCache addressCache;

    @Test
    void shouldBeIgnored_whenCacheNotSet() {
        Mockito.when(addressCache.isActiveNewAddressOperation(anyLong())).thenReturn(false);

        newAddressHandler.handleUpdate(bot, message);

        Mockito.verify(addressCache, times(1)).isActiveNewAddressOperation(message.getChatId());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Челябинск, Пионерская", "Челябинск, Пионерская, А", "aregaergar", ",", ",,", ",,,", "1", "-2", "2.0"
    })
    void shouldBeIncorrectAddress(String address) {
        Mockito.when(addressCache.isActiveNewAddressOperation(anyLong())).thenReturn(true);
        Mockito.doNothing().when(bot).executeAsync(eq("Неверный формат адреса!"), any(Message.class));

        message.setText(address);

        newAddressHandler.handleUpdate(bot, message);

        Mockito.verify(addressCache, times(1)).isActiveNewAddressOperation(message.getChatId());
        Mockito.verify(bot, times(1)).executeAsync("Неверный формат адреса!", message);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Челябинск, Пионерская, 3А", "Челябинск,Пионерская,3А", "челябинск,пионерская,3а",
            "Россия, Челябинск, Пионерская, 3А",
    })
    void shouldBeCorrectAddress(String address) {
        Mockito.when(addressCache.isActiveNewAddressOperation(anyLong())).thenReturn(true);
        Mockito.doNothing().when(bot).executeAsync(eq("Верно!"), any(Message.class));
        Mockito.doNothing().when(addressCache).setActivatedNewAddressOperation(anyLong(), eq(false));

        message.setText(address);

        newAddressHandler.handleUpdate(bot, message);

        Mockito.verify(addressCache, times(1)).isActiveNewAddressOperation(message.getChatId());
        Mockito.verify(bot, times(1)).executeAsync("Верно!", message);
        Mockito.verify(addressCache, times(1)).setActivatedNewAddressOperation(message.getChatId(), false);
    }
}
