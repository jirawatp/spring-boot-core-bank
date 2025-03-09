package com.pattanayutanachot.jirawat.core.bank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @Value("${spring.application.name:core-bank}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @BeforeEach
    void setUp() {
        HealthCheckController healthCheckController = new HealthCheckController(jdbcTemplate, redisConnectionFactory);
        mockMvc = MockMvcBuilders.standaloneSetup(healthCheckController).build();
    }

    @Test
    void healthCheck_ShouldReturnUPStatus_WhenAllServicesAreAvailable() throws Exception {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);
        when(redisConnectionFactory.getConnection()).thenReturn(mock());

        mockMvc.perform(get("/api/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").value(applicationName))
                .andExpect(jsonPath("$.version").value(applicationVersion))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database").value("UP"))
                .andExpect(jsonPath("$.redis").value("UP"));
    }

    @Test
    void healthCheck_ShouldReturnDatabaseDown_WhenDatabaseIsUnavailable() throws Exception {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenThrow(new RuntimeException("Database down"));
        when(redisConnectionFactory.getConnection()).thenReturn(mock());

        mockMvc.perform(get("/api/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database").value("DOWN"))
                .andExpect(jsonPath("$.redis").value("UP"));
    }

    @Test
    void healthCheck_ShouldReturnRedisDown_WhenRedisIsUnavailable() throws Exception {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);
        when(redisConnectionFactory.getConnection()).thenThrow(new RuntimeException("Redis down"));

        mockMvc.perform(get("/api/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database").value("UP"))
                .andExpect(jsonPath("$.redis").value("DOWN"));
    }

    @Test
    void healthCheck_ShouldReturnBothDown_WhenBothServicesAreUnavailable() throws Exception {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenThrow(new RuntimeException("Database down"));
        when(redisConnectionFactory.getConnection()).thenThrow(new RuntimeException("Redis down"));

        mockMvc.perform(get("/api/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database").value("DOWN"))
                .andExpect(jsonPath("$.redis").value("DOWN"));
    }
}