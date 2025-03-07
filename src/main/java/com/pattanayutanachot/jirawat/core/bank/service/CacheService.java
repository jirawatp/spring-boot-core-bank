package com.pattanayutanachot.jirawat.core.bank.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);
    private final StringRedisTemplate redisTemplate;

    public CacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void set(String key, String value, long duration, TimeUnit timeUnit) {
        logger.info("Caching key: {} for {} {}", key, duration, timeUnit);
        redisTemplate.opsForValue().set(key, value, duration, timeUnit);
    }

    public String get(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            logger.info("Cache hit for key: {}", key);
        } else {
            logger.warn("Cache miss for key: {}", key);
        }
        return value;
    }

    public void delete(String key) {
        logger.info("Deleting cache key: {}", key);
        redisTemplate.delete(key);
    }
}