package com.pattanayutanachot.jirawat.core.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

class CacheServiceTest {

    @InjectMocks
    private CacheService cacheService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void set_CacheEntry_Success() {
        String key = "testKey";
        String value = "testValue";
        long duration = 60;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        cacheService.set(key, value, duration, timeUnit);

        verify(valueOperations, times(1)).set(key, value, duration, timeUnit);
    }

    @Test
    void get_CacheHit_Success() {
        String key = "testKey";
        String expectedValue = "testValue";

        when(valueOperations.get(key)).thenReturn(expectedValue);

        String result = cacheService.get(key);

        verify(valueOperations, times(1)).get(key);
        assert result.equals(expectedValue);
    }

    @Test
    void get_CacheMiss_ReturnsNull() {
        String key = "missingKey";

        when(valueOperations.get(key)).thenReturn(null);

        String result = cacheService.get(key);

        verify(valueOperations, times(1)).get(key);
        assert result == null;
    }

    @Test
    void delete_CacheEntry_Success() {
        String key = "testKey";

        cacheService.delete(key);

        verify(redisTemplate, times(1)).delete(key);
    }
}