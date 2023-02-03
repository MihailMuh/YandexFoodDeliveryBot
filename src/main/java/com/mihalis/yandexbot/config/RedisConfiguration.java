package com.mihalis.yandexbot.config;

import com.mihalis.yandexbot.model.Address;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisSocketConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
class RedisConfiguration {
    @Primary
    @Bean(name = "addressConnection")
    public LettuceConnectionFactory getAddressConnection(
            @Value("${spring.data.redis.socket}") String unixSocketPath, @Value("${spring.data.redis.database.old-address}") int database) {

        return getConnection(unixSocketPath, database);
    }

    @Bean(name = "addressStateConnection")
    public LettuceConnectionFactory getAddressStateConnection(
            @Value("${spring.data.redis.socket}") String unixSocketPath, @Value("${spring.data.redis.database.new-address}") int database) {

        return getConnection(unixSocketPath, database);
    }

    @Bean(name = "addressStorage")
    public ValueOperations<String, Address> getAddressStorage(@Qualifier("addressConnection")
                                                                 LettuceConnectionFactory addressConnection) {
        RedisTemplate<String, Address> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(addressConnection);
        redisTemplate.setKeySerializer(new Jackson2JsonRedisSerializer<>(String.class));
        redisTemplate.afterPropertiesSet();

        return redisTemplate.opsForValue();
    }

    @Bean(name = "addressStateStorage")
    public ValueOperations<Long, Boolean> getAddressStateStorage(@Qualifier("addressStateConnection")
                                                                   LettuceConnectionFactory newAddressConnection) {
        RedisTemplate<Long, Boolean> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(newAddressConnection);
        redisTemplate.afterPropertiesSet();

        return redisTemplate.opsForValue();
    }

    private LettuceConnectionFactory getConnection(String unixSocketPath, int database) {
        val redis = new RedisSocketConfiguration();
        redis.setSocket(unixSocketPath);
        redis.setDatabase(database);

        return new LettuceConnectionFactory(redis);
    }
}
