package com.pattanayutanachot.jirawat.core.bank.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {

    @InjectMocks
    private RedisConfig redisConfig;

    @Test
    void testRedisConnectionFactoryCreation() {
        RedisConnectionFactory factory = redisConfig.redisConnectionFactory();
        assertNotNull(factory);
        assertTrue(factory instanceof LettuceConnectionFactory, "Expected a LettuceConnectionFactory");
    }
}