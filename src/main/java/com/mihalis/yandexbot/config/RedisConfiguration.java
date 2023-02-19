package com.mihalis.yandexbot.config;

import com.mihalis.yandexbot.model.Address;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSocketConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
class RedisConfiguration {
    @Value("${spring.data.redis.socket}")
    private String unixSocketPath;

    @Bean
    public ValueOperations<String, Address> getAddressStorage(@Value("${spring.data.redis.database.address}") int database) {
        return new Redis<Address>().getOperations(Address.class, unixSocketPath, database).opsForValue();
    }

    @Bean
    public HashOperations<String, String, Object> getAddressStateStorage(@Value("${spring.data.redis.database.fsm}") int database) {
        return new Redis<>().getOperations(Object.class, unixSocketPath, database).opsForHash();
    }

    @Bean
    public ValueOperations<String, String> getProfileStorage(@Value("${spring.data.redis.database.profile}") int database) {
        return new Redis<String>().getOperations(String.class, unixSocketPath, database).opsForValue();
    }

    private static class Redis<V> {
        RedisTemplate<String, V> getOperations(Class<V> valueClass, String unixSocketPath, int database) {
            RedisSocketConfiguration redis = new RedisSocketConfiguration();
            redis.setSocket(unixSocketPath);
            redis.setDatabase(database);

            LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redis);
            lettuceConnectionFactory.afterPropertiesSet();

            RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(lettuceConnectionFactory);
            redisTemplate.setKeySerializer(new Jackson2JsonRedisSerializer<>(String.class));
            redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(valueClass));
            redisTemplate.setStringSerializer(new Jackson2JsonRedisSerializer<>(String.class));
            redisTemplate.setHashKeySerializer(new Jackson2JsonRedisSerializer<>(String.class));
            redisTemplate.afterPropertiesSet();

            return redisTemplate;
        }
    }
}
