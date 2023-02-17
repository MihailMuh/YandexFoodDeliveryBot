package com.mihalis.yandexbot.config;

import com.mihalis.yandexbot.model.Address;
import net.jodah.typetools.TypeResolver;
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
        return new Redis<String, Address>().getOperations(unixSocketPath, database).opsForValue();
    }

    @Bean
    public HashOperations<String, String, Object> getAddressStateStorage(@Value("${spring.data.redis.database.fsm}") int database) {
        return new Redis<String, Object>().getOperations(unixSocketPath, database).opsForHash();
    }

    private static class Redis<K, V> {
        private final Class<?>[] typeArguments = TypeResolver.resolveRawArguments(Redis.class, getClass());

        RedisTemplate<K, V> getOperations(String unixSocketPath, int database) {
            RedisSocketConfiguration redis = new RedisSocketConfiguration();
            redis.setSocket(unixSocketPath);
            redis.setDatabase(database);

            LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redis);
            lettuceConnectionFactory.afterPropertiesSet();

            RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(lettuceConnectionFactory);
            redisTemplate.setKeySerializer(getSerializer(0));
            redisTemplate.setValueSerializer(getSerializer(1));
            redisTemplate.afterPropertiesSet();

            return redisTemplate;
        }

        private Jackson2JsonRedisSerializer<?> getSerializer(int typeArgumentIndex) {
            return new Jackson2JsonRedisSerializer<>(typeArguments[typeArgumentIndex]);
        }
    }
}
