package com.mihalis.yandexbot.commands;

import com.mihalis.yandexbot.beans.Telegram;
import com.mihalis.yandexbot.cache.AddressCache;
import com.mihalis.yandexbot.cache.SelectNewAddressCache;
import com.mihalis.yandexbot.telegram.Bot;
import com.mihalis.yandexbot.telegram.PostMessage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {Telegram.class, AddressCommand.class, InlineKeyboardMarkup.class})
public class AddressCommandTests {
    @Autowired
    private AddressCommand startCommand;

    @Autowired
    private Message message;

    @MockBean
    private AddressCache addressesCache;

    @MockBean
    private SelectNewAddressCache selectNewAddressCache;

    @MockBean
    private Bot bot;

    @Test
    void shouldSendUserAddress_whenItExists() {
        Mockito.when(addressesCache.getAddress(anyLong())).thenReturn(new AddressCache.Address("Hello", "World!"));
        Mockito.doNothing().when(bot).executeAsync(eq("Твой текущий адрес: Hello"), any(Message.class));

        startCommand.answer(bot, message);

        Mockito.verify(bot, times(1)).executeAsync("Твой текущий адрес: Hello", message);
        Mockito.verify(addressesCache, times(1)).getAddress(message.getChatId());
    }

    @Test
    void shouldNotSendUserAddress_whenItNotExists() {
        Mockito.when(addressesCache.getAddress(anyLong())).thenReturn(new AddressCache.Address("", ""));

        startCommand.answer(bot, message);

        Mockito.verify(bot, never()).executeAsync("Твой текущий адрес: ", message);
        Mockito.verify(addressesCache, times(1)).getAddress(message.getChatId());
    }

    @Test
    @SneakyThrows
    void shouldAnswer() {
        Mockito.when(addressesCache.getAddress(anyLong())).thenReturn(new AddressCache.Address("", ""));
        Mockito.when(bot.executeAsync(any(PostMessage.class))).thenReturn(new CompletableFuture<>());
        Mockito.doNothing().when(selectNewAddressCache).setActivatedNewAddressOperation(anyLong(), eq(true));

        startCommand.answer(bot, message);

        Mockito.verify(addressesCache, times(1)).getAddress(message.getChatId());
        Mockito.verify(bot, times(1)).executeAsync(any(PostMessage.class));
        Mockito.verify(selectNewAddressCache, times(1))
                .setActivatedNewAddressOperation(message.getChatId(), true);
    }
}
