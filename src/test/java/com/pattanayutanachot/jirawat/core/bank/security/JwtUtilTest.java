package com.pattanayutanachot.jirawat.core.bank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String SECRET_KEY = "your-very-secret-key-your-very-secret-key"; // Same as in JwtUtil
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        userDetails = mock(UserDetails.class);
    }

    @Test
    void testGenerateToken() {
        when(userDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        when(userDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtUtil.generateToken(userDetails);

        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals("test@example.com", extractedUsername);
    }

    @Test
    void testExtractExpiration() {
        when(userDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtUtil.generateToken(userDetails);

        Date expiration = jwtUtil.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateToken_ValidToken() {
        when(userDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtUtil.generateToken(userDetails);

        boolean isValid = jwtUtil.validateToken(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        when(userDetails.getUsername()).thenReturn("test@example.com");
        String invalidToken = Jwts.builder()
                .setSubject("fake@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        boolean isValid = jwtUtil.validateToken(invalidToken, userDetails);
        assertFalse(isValid);
    }
}