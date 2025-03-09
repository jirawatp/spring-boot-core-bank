package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CompleteProfileRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void completeProfileRequest_ShouldBeValid() {
        CompleteProfileRequest request = new CompleteProfileRequest(
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                "123456"
        );

        Set<ConstraintViolation<CompleteProfileRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void completeProfileRequest_ShouldFail_WhenCitizenIdIsInvalid() {
        CompleteProfileRequest request = new CompleteProfileRequest(
                "12345678", // Invalid: Not 13 digits
                "สมชาย ใจดี",
                "Somchai Jaidee",
                "123456"
        );

        Set<ConstraintViolation<CompleteProfileRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Citizen ID must be 13 digits", violations.iterator().next().getMessage());
    }

    @Test
    void completeProfileRequest_ShouldFail_WhenThaiNameIsBlank() {
        CompleteProfileRequest request = new CompleteProfileRequest(
                "1234567890123",
                "",
                "Somchai Jaidee",
                "123456"
        );

        Set<ConstraintViolation<CompleteProfileRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Thai name is required", violations.iterator().next().getMessage());
    }

    @Test
    void completeProfileRequest_ShouldFail_WhenEnglishNameIsBlank() {
        CompleteProfileRequest request = new CompleteProfileRequest(
                "1234567890123",
                "สมชาย ใจดี",
                "",
                "123456"
        );

        Set<ConstraintViolation<CompleteProfileRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("English name is required", violations.iterator().next().getMessage());
    }

    @Test
    void completeProfileRequest_ShouldFail_WhenPinIsInvalid() {
        CompleteProfileRequest request = new CompleteProfileRequest(
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                "12345" // Invalid: Not 6 digits
        );

        Set<ConstraintViolation<CompleteProfileRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("PIN must be 6 digits", violations.iterator().next().getMessage());
    }
}