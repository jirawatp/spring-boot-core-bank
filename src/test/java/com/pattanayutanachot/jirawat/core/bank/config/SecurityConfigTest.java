package com.pattanayutanachot.jirawat.core.bank.config;

import com.pattanayutanachot.jirawat.core.bank.security.CustomUserDetailsService;
import com.pattanayutanachot.jirawat.core.bank.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        CustomUserDetailsService mockUserDetailsService = mock(CustomUserDetailsService.class);
        JwtAuthenticationFilter mockJwtAuthenticationFilter = mock(JwtAuthenticationFilter.class);
        securityConfig = new SecurityConfig(mockUserDetailsService, mockJwtAuthenticationFilter);
    }

    @Test
    void securityFilterChain_ShouldBeCreatedSuccessfully() throws Exception {
        SecurityFilterChain securityFilterChain = securityConfig.securityFilterChain(Mockito.mock(org.springframework.security.config.annotation.web.builders.HttpSecurity.class));
        assertThat(securityFilterChain).isNotNull();
    }

    @Test
    void authenticationManager_ShouldReturnProviderManager() {
        AuthenticationManager authenticationManager = securityConfig.authenticationManager();
        assertThat(authenticationManager).isInstanceOf(ProviderManager.class);
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder.encode("test")).isNotEmpty();
    }
}