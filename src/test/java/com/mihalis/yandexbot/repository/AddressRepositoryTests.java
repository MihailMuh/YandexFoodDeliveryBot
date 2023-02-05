package com.mihalis.yandexbot.repository;

import com.mihalis.yandexbot.beans.AddressBeans;
import com.mihalis.yandexbot.beans.TelegramBeans;
import com.mihalis.yandexbot.model.Address;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.util.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {TelegramBeans.class, AddressRepository.class, AddressBeans.class})
public class AddressRepositoryTests {
    @Autowired
    private AddressRepository addressRepository;

    @MockBean
    private ValueOperations<String, Address> addressStorage;

    private RedisOperations<String, Address> redisOperations;

    @SneakyThrows
    @PostConstruct
    void getRedisOperations() {
        Field redisOperationsField = AddressRepository.class.getDeclaredField("redisOperations");
        redisOperationsField.setAccessible(true);

        redisOperations = Mockito.mock(RedisTemplate.class);
        redisOperationsField.set(addressRepository, redisOperations);
    }

    @Test
    void shouldSaveAddress(@Autowired Address address) {
        Mockito.doNothing().when(addressStorage).set(anyString(), any(Address.class));

        addressRepository.setAddress(1234567890L, address);

        Mockito.verify(addressStorage, times(1)).set("1234567890", address);
    }

    @ParameterizedTest
    @MethodSource("getAddresses")
    void shouldGetNonNullAddress(Address address) {
        Mockito.when(addressStorage.get(anyLong())).thenReturn(address);

        Assertions.assertNotNull(addressRepository.getAddress(1234567890L));

        Mockito.verify(addressStorage, times(1)).get("1234567890");
    }

    @Test
    void shouldHasKey() {
        Mockito.when(redisOperations.hasKey(anyString())).thenReturn(true);

        Assertions.assertTrue(addressRepository.hasAddress(1234567890L));

        Mockito.verify(redisOperations, times(1)).hasKey("1234567890");
    }

    @Test
    void shouldDeleteAddress() {
        Mockito.when(redisOperations.delete(anyString())).thenReturn(true);

        addressRepository.deleteAddress(1234567890L);

        Mockito.verify(redisOperations, times(1)).delete("1234567890");
    }

    @ParameterizedTest
    @MethodSource("getItems")
    void shouldGetItems(Pair<Set<String>, List<Address>> pair) {
        Mockito.when(redisOperations.keys("*")).thenReturn(pair.getFirst());
        Mockito.when(addressStorage.multiGet(eq(pair.getFirst()))).thenReturn(pair.getSecond());

        Assertions.assertInstanceOf(ArrayList.class, addressRepository.items());

        Mockito.verify(redisOperations, times(1)).keys("*");
        Mockito.verify(addressStorage, times(1)).multiGet(pair.getFirst().parallelStream().toList());
    }

    private static Stream<Address> getAddresses() {
        return Stream.of(Address.empty(), null);
    }

    private static Stream<Pair<Set<String>, List<Address>>> getItems() {
        return Stream.of(
                Pair.of(
                        new LinkedHashSet<>(777),
                        new ArrayList<>(777)
                ),
                Pair.of(
                        new LinkedHashSet<>(0),
                        new ArrayList<>(0)
                )
        );
    }
}
