package com.mihalis.yandexbot.config;

import com.mihalis.yandexbot.cache.Address;
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
            @Value("${spring.data.redis.socket}") String unixSocketPath,
            @Value("${spring.data.redis.database.old-address}") int database) {

        return getConnection(unixSocketPath, database);
    }

    @Bean(name = "newAddressConnection")
    public LettuceConnectionFactory getNewAddressConnection(
            @Value("${spring.data.redis.socket}") String unixSocketPath,
            @Value("${spring.data.redis.database.new-address}") int database) {

        return getConnection(unixSocketPath, database);
    }

    @Bean(name = "addressOperations")
    public ValueOperations<String, Address> getAddressOperations(@Qualifier("addressConnection")
                                                                 LettuceConnectionFactory addressesConnection) {
        RedisTemplate<String, Address> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(addressesConnection);
        redisTemplate.setKeySerializer(new Jackson2JsonRedisSerializer<>(String.class));
        redisTemplate.afterPropertiesSet();

        return redisTemplate.opsForValue();
    }

    @Bean(name = "newAddressOperation")
    public ValueOperations<String, Boolean> getNewAddressOperation(@Qualifier("newAddressConnection")
                                                                   LettuceConnectionFactory newAddressConnection) {
        RedisTemplate<String, Boolean> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(newAddressConnection);
        redisTemplate.setKeySerializer(new Jackson2JsonRedisSerializer<>(String.class));
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
