package com.mihalis.yandexbot.config;

import com.mihalis.yandexbot.cache.AddressCache;
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
    public ValueOperations<Long, AddressCache.Address> getAddressOperations(@Qualifier("addressConnection")
                                                                                  LettuceConnectionFactory addressesConnection) {
        RedisTemplate<Long, AddressCache.Address> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(addressesConnection);
        redisTemplate.afterPropertiesSet();

        return redisTemplate.opsForValue();
    }

    @Bean(name = "newAddressOperation")
    public ValueOperations<Long, Boolean> getNewAddressOperation(@Qualifier("newAddressConnection")
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
