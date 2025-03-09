package com.pattanayutanachot.jirawat.core.bank.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import static org.assertj.core.api.Assertions.assertThat;

class RedisConfigTest {

    @Test
    void redisConnectionFactory_ShouldBeCreated_WhenNoOtherBeanExists() {
        ApplicationContext context = new AnnotationConfigApplicationContext(RedisConfig.class);

        RedisConnectionFactory redisConnectionFactory = context.getBean(RedisConnectionFactory.class);
        assertThat(redisConnectionFactory).isNotNull();
    }
}