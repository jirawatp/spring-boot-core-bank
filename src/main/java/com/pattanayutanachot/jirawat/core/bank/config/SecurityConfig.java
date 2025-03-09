package com.pattanayutanachot.jirawat.core.bank.config;

import com.pattanayutanachot.jirawat.core.bank.security.CustomUserDetailsService;
import com.pattanayutanachot.jirawat.core.bank.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public authentication endpoints
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()

                        // Restricted admin actions
                        .requestMatchers("/api/auth/register-teller").hasAuthority("ROLE_SUPER_ADMIN")

                        // Authenticated user actions
                        .requestMatchers("/api/auth/complete-profile", "/api/auth/logout").authenticated()

                        // Role-based access control for different users
                        .requestMatchers("/api/customer/**").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers("/api/teller/**").hasAuthority("ROLE_TELLER")
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_SUPER_ADMIN")

                        // Ensure only tellers can see all transactions and deposit
                        .requestMatchers("/api/account/deposit", "/api/transaction/transactions").hasAuthority("ROLE_TELLER")

                        // Ensure only tellers can create accounts
                        .requestMatchers("/api/account/create").hasAuthority("ROLE_TELLER")

                        // Swagger access (OPTIONAL: Restrict this to development mode)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()

                        // Ensure all other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(List.of(provider));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}