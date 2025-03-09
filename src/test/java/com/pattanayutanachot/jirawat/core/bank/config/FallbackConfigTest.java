package com.pattanayutanachot.jirawat.core.bank.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private FallbackConfig fallbackConfig;

    @Mock
    private DataSourceProperties mockDataSourceProperties;

    @Mock
    private FlywayProperties mockFlywayProperties;

    @BeforeEach
    void setUp() {
        fallbackConfig = new FallbackConfig();
    }

    @Test
    void testDataSourceCreation_ValidConfig() {
        when(mockDataSourceProperties.getUrl()).thenReturn("jdbc:postgresql://localhost:5432/testdb");
        when(mockDataSourceProperties.getUsername()).thenReturn("testuser");
        when(mockDataSourceProperties.getPassword()).thenReturn("testpass");
        when(mockDataSourceProperties.getDriverClassName()).thenReturn("org.postgresql.Driver");

        DataSource dataSource = fallbackConfig.dataSource(mockDataSourceProperties);
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof DriverManagerDataSource);

        DriverManagerDataSource ds = (DriverManagerDataSource) dataSource;
        assertEquals("jdbc:postgresql://localhost:5432/testdb", ds.getUrl());
        assertEquals("testuser", ds.getUsername());
        assertEquals("testpass", ds.getPassword());
        assertEquals("org.postgresql.Driver", mockDataSourceProperties.getDriverClassName());
    }

    @Test
    void testDataSourceCreation_ThrowsExceptionWhenUrlIsMissing() {
        when(mockDataSourceProperties.getUrl()).thenReturn(null);
        when(mockDataSourceProperties.getUsername()).thenReturn("testuser");
        when(mockDataSourceProperties.getPassword()).thenReturn("testpass");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            fallbackConfig.dataSource(mockDataSourceProperties);
        });

        assertEquals("Database URL (spring.datasource.url or DB_URL) is not set!", exception.getMessage());
    }

    @Test
    void testFlywayBeanCreation() {
        DataSource mockDataSource = mock(DataSource.class);
        when(mockFlywayProperties.getLocations()).thenReturn(Collections.singletonList("classpath:db/migration"));

        Flyway flyway = fallbackConfig.flyway(mockDataSource, mockFlywayProperties);
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