package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void registerRequest_ShouldBeValid() {
        RegisterRequest request = new RegisterRequest("test@example.com", "password123");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "RegisterRequest should be valid");
    }

    @Test
    void registerRequest_ShouldFailForInvalidEmail() {
        RegisterRequest request = new RegisterRequest("invalid-email", "password123");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "RegisterRequest should fail for invalid email");
        assertEquals(1, violations.size());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void registerRequest_ShouldFailForShortPassword() {
        RegisterRequest request = new RegisterRequest("test@example.com", "short");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "RegisterRequest should fail for short password");
        assertEquals(1, violations.size());
        assertEquals("Password must be at least 8 characters", violations.iterator().next().getMessage());
    }
}