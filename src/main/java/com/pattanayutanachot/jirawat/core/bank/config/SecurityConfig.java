package com.pattanayutanachot.jirawat.core.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for APIs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // Public authentication endpoints
                        .requestMatchers("/api/private/**").hasRole("USER")  // Private API requires USER role
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v2/api-docs", "/swagger-resources/**").permitAll() // Allow Swagger
                        .requestMatchers("/api/health").permitAll() // Allow Health Check
                        .anyRequest().authenticated()  // All other requests need authentication
                );

        return http.build();
    }
}