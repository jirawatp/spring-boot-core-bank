package com.pattanayutanachot.jirawat.core.bank.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

@Configuration
@Import(FlywayAutoConfiguration.class)
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource, FlywayProperties properties) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(properties.getLocations().toArray(new String[0]))
                .baselineOnMigrate(properties.isBaselineOnMigrate())
                .outOfOrder(properties.isOutOfOrder())
                .load();
    }
}