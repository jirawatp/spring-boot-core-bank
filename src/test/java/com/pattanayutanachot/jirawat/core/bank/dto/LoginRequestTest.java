package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void loginRequest_ShouldBeValid() {
        LoginRequest request = new LoginRequest("test@example.com", "securePassword");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void loginRequest_ShouldFail_WhenEmailIsBlank() {
        LoginRequest request = new LoginRequest("", "securePassword");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Email is required", violations.iterator().next().getMessage());
    }

    @Test
    void loginRequest_ShouldFail_WhenPasswordIsBlank() {
        LoginRequest request = new LoginRequest("test@example.com", "");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }
}