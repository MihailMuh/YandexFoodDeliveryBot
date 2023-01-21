package com.mihalis.yandexbot.cache;

import com.mihalis.yandexbot.beans.Telegram;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {Telegram.class, SelectNewAddressCache.class})
public class SelectNewAddressCacheTests {
    @Autowired
    private SelectNewAddressCache selectNewAddressCache;

    @MockBean
    private ValueOperations<Long, Boolean> newAddressOperation;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldActivateOperationInCache(boolean value) {
        Mockito.doNothing().when(newAddressOperation).set(anyLong(), anyBoolean());

        selectNewAddressCache.setActivatedNewAddressOperation(1234567890L, value);

        Mockito.verify(newAddressOperation, times(1)).set(1234567890L, value);
    }

    @Test
    void shouldGetStateOfOperationFromCache() {
        Mockito.when(newAddressOperation.get(anyLong())).thenReturn(anyBoolean());

        selectNewAddressCache.isActiveNewAddressOperation(1234567890L);

        Mockito.verify(newAddressOperation, times(1)).get(1234567890L);
    }
}
