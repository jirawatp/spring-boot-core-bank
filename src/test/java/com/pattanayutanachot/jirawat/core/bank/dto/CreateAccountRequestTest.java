package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateAccountRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createAccountRequest_ShouldBeValid() {
        CreateAccountRequest request = new CreateAccountRequest(
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                BigDecimal.valueOf(1000)
        );

        Set<ConstraintViolation<CreateAccountRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void createAccountRequest_ShouldFail_WhenCitizenIdIsBlank() {
        CreateAccountRequest request = new CreateAccountRequest(
                "",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                BigDecimal.valueOf(1000)
        );

        Set<ConstraintViolation<CreateAccountRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Citizen ID is required", violations.iterator().next().getMessage());
    }

    @Test
    void createAccountRequest_ShouldFail_WhenThaiNameIsBlank() {
        CreateAccountRequest request = new CreateAccountRequest(
                "1234567890123",
                "",
                "Somchai Jaidee",
                BigDecimal.valueOf(1000)
        );

        Set<ConstraintViolation<CreateAccountRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Thai name is required", violations.iterator().next().getMessage());
    }

    @Test
    void createAccountRequest_ShouldFail_WhenEnglishNameIsBlank() {
        CreateAccountRequest request = new CreateAccountRequest(
                "1234567890123",
                "สมชาย ใจดี",
                "",
                BigDecimal.valueOf(1000)
        );

        Set<ConstraintViolation<CreateAccountRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("English name is required", violations.iterator().next().getMessage());
    }

    @Test
    void createAccountRequest_ShouldFail_WhenInitialDepositIsNull() {
        CreateAccountRequest request = new CreateAccountRequest(
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                null
        );

        Set<ConstraintViolation<CreateAccountRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Initial deposit is required", violations.iterator().next().getMessage());
    }

    @Test
    void createAccountRequest_ShouldFail_WhenInitialDepositIsNegative() {
        CreateAccountRequest request = new CreateAccountRequest(
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                BigDecimal.valueOf(-100)
        );

        Set<ConstraintViolation<CreateAccountRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Initial deposit must be zero or positive", violations.iterator().next().getMessage());
    }
}