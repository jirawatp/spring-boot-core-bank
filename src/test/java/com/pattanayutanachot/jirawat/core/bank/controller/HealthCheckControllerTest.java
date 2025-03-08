package com.pattanayutanachot.jirawat.core.bank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

class HealthCheckControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @Mock
    private RedisConnection redisConnection;

    @InjectMocks
    private HealthCheckController healthCheckController;

    @Value("${spring.application.name:test-app}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(healthCheckController).build();

        healthCheckController = new HealthCheckController(jdbcTemplate, redisConnectionFactory);
    }

    @Test
    void testHealthCheck_AllServicesUp() throws Exception {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);
        when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
        when(redisConnection.ping()).thenReturn("PONG");

        mockMvc.perform(get("/api/health").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application", is(applicationName)))
                .andExpect(jsonPath("$.version", is(applicationVersion)))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.database", is("UP")))
                .andExpect(jsonPath("$.redis", is("UP")));

        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class));
        verify(redisConnectionFactory, times(1)).getConnection();
        verify(redisConnection, times(1)).ping();
    }

    @Test
    void testHealthCheck_DatabaseDown() throws Exception {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenThrow(new RuntimeException("DB Error"));
        when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
        when(redisConnection.ping()).thenReturn("PONG");

        mockMvc.perform(get("/api/health").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application", is(applicationName)))
                .andExpect(jsonPath("$.version", is(applicationVersion)))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.database", is("DOWN")))
                .andExpect(jsonPath("$.redis", is("UP")));

        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class));
        verify(redisConnectionFactory, times(1)).getConnection();
        verify(redisConnection, times(1)).ping();
    }

    @Test
    void testHealthCheck_RedisDown() throws Exception {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);
        when(redisConnectionFactory.getConnection()).thenThrow(new RuntimeException("Redis Error"));

        mockMvc.perform(get("/api/health").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application", is(applicationName)))
                .andExpect(jsonPath("$.version", is(applicationVersion)))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.database", is("UP")))
                .andExpect(jsonPath("$.redis", is("DOWN")));

        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class));
        verify(redisConnectionFactory, times(1)).getConnection();
    }

    @Test
    void testHealthCheck_BothDatabaseAndRedisDown() throws Exception {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenThrow(new RuntimeException("DB Error"));
        when(redisConnectionFactory.getConnection()).thenThrow(new RuntimeException("Redis Error"));

        mockMvc.perform(get("/api/health").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application", is(applicationName)))
                .andExpect(jsonPath("$.version", is(applicationVersion)))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.database", is("DOWN")))
                .andExpect(jsonPath("$.redis", is("DOWN")));

        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class));
        verify(redisConnectionFactory, times(1)).getConnection();
    }
}