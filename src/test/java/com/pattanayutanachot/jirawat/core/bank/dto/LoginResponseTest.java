package com.pattanayutanachot.jirawat.core.bank.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void loginResponse_ShouldCreateCorrectObject() {
        String token = "test-jwt-token";
        String email = "test@example.com";

        LoginResponse response = new LoginResponse(token, email);

        assertNotNull(response);
        assertEquals(token, response.token());
        assertEquals(email, response.email());
    }

    @Test
    void loginResponse_ShouldHandleNullValues() {
        LoginResponse response = new LoginResponse(null, null);

        assertNull(response.token());
        assertNull(response.email());
    }
}