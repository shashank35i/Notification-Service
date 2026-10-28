package com.goon.notificationservice.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean(destroyMethod = "shutdown")
    RedisClient redisClient(
            @Value("${spring.data.redis.url:redis://localhost:6379}") String redisUrl) {
        return RedisClient.create(redisUrl);
    }

    @Bean(destroyMethod = "close")
    StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
        return redisClient.connect();
    }
}
