package com.pattanayutanachot.jirawat.core.bank.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FallbackConfigTest {

    @InjectMocks
    private FallbackConfig fallbackConfig;

    @Mock
    private DataSourceProperties dataSourceProperties;

    @Mock
    private FlywayProperties flywayProperties;

    @Test
    void testDataSourceCreation() {
        // Mock DataSource properties
        when(dataSourceProperties.getUrl()).thenReturn("jdbc:postgresql://localhost:5432/testdb");
        when(dataSourceProperties.getUsername()).thenReturn("testuser");
        when(dataSourceProperties.getPassword()).thenReturn("testpassword");
        when(dataSourceProperties.getDriverClassName()).thenReturn("org.postgresql.Driver");

        DataSource dataSource = fallbackConfig.dataSource(dataSourceProperties);
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof DriverManagerDataSource);

        DriverManagerDataSource ds = (DriverManagerDataSource) dataSource;
        assertEquals("jdbc:postgresql://localhost:5432/testdb", ds.getUrl());
        assertEquals("testuser", ds.getUsername());
    }

    @Test
    void testDataSourceCreationFailsWhenNoUrl() {
        // Simulate missing URL
        when(dataSourceProperties.getUrl()).thenReturn(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> fallbackConfig.dataSource(dataSourceProperties));

        assertEquals("Database URL (spring.datasource.url or DB_URL) is not set!", exception.getMessage());
    }

    @Test
    void testFlywayCreation() {
        // Mock Flyway properties
        when(flywayProperties.getLocations()).thenReturn(Collections.singletonList("classpath:db/migration"));

        DataSource mockDataSource = mock(DataSource.class);
        Flyway flyway = fallbackConfig.flyway(mockDataSource, flywayProperties);

        assertNotNull(flyway);
    }

    @Test
    void testJdbcTemplateCreation() {
        DataSource mockDataSource = mock(DataSource.class);
        JdbcTemplate jdbcTemplate = fallbackConfig.jdbcTemplate(mockDataSource);
        assertNotNull(jdbcTemplate);
    }

    @Test
    void testRedisConnectionFactoryCreation() {
        RedisConnectionFactory redisConnectionFactory = fallbackConfig.redisConnectionFactory();
        assertNotNull(redisConnectionFactory);
    }
}