package com.pattanayutanachot.jirawat.core.bank.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Optional;

@Configuration
@EnableConfigurationProperties(FlywayProperties.class) // Explicitly enable FlywayProperties
public class FallbackConfig {

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(DataSourceProperties properties) {
        String url = Optional.ofNullable(properties.getUrl()).orElse(System.getenv("DB_URL"));
        String username = Optional.ofNullable(properties.getUsername()).orElse(System.getenv("DB_USERNAME"));
        String password = Optional.ofNullable(properties.getPassword()).orElse(System.getenv("DB_PASSWORD"));
        String driverClassName = Optional.ofNullable(properties.getDriverClassName()).orElse("org.postgresql.Driver");

        if (url == null || url.isEmpty()) {
            throw new IllegalStateException("Database URL (spring.datasource.url or DB_URL) is not set!");
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean
    @ConditionalOnMissingBean(Flyway.class)
    public Flyway flyway(DataSource dataSource, FlywayProperties flywayProperties) {
        return Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(dataSource)
                .locations(flywayProperties.getLocations().toArray(new String[0]))
                .load();
    }

    @Bean
    @ConditionalOnMissingBean(JdbcTemplate.class)
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }
}