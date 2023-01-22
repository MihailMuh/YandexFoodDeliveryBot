package com.mihalis.yandexbot.cache;

import com.mihalis.yandexbot.beans.Telegram;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ValueOperations;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {Telegram.class, AddressCache.class})
public class AddressCacheTests {
    @Autowired
    private AddressCache addressCache;

    @MockBean
    private ValueOperations<Long, AddressCache.Address> addressesOperations;

    @Test
    void shouldSaveAddressToCache() {
        Mockito.doNothing().when(addressesOperations).set(anyLong(), any(AddressCache.Address.class));

        addressCache.setAddress(1234567890L, "Челябинск, Пионерская, 3А");

        Mockito.verify(addressesOperations, times(1))
                .set(eq(1234567890L), any(AddressCache.Address.class));
    }

    @ParameterizedTest
    @MethodSource("getAddresses")
    void shouldGetNonNullAddressFromCache(AddressCache.Address address) {
        Mockito.when(addressesOperations.get(anyLong())).thenReturn(address);

        Assertions.assertNotNull(addressCache.getAddress(1234567890L));

        Mockito.verify(addressesOperations, times(1)).get(1234567890L);
    }

    private static Stream<AddressCache.Address> getAddresses() {
        return Stream.of(new AddressCache.Address("", ""), null);
    }
}
