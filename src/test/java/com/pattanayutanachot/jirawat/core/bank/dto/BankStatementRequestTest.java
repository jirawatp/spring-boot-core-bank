package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BankStatementRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void bankStatementRequest_ShouldBeValid() {
        BankStatementRequest request = new BankStatementRequest(2025, 3, "1234567", "1234");

        Set<ConstraintViolation<BankStatementRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void bankStatementRequest_ShouldFailWhenYearIsNull() {
        BankStatementRequest request = new BankStatementRequest(null, 3, "1234567", "1234");

        Set<ConstraintViolation<BankStatementRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("Year is required", violations.iterator().next().getMessage());
    }

    @Test
    void bankStatementRequest_ShouldFailWhenYearIsInvalid() {
        BankStatementRequest request = new BankStatementRequest(1800, 3, "1234567", "1234");

        Set<ConstraintViolation<BankStatementRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("Invalid year", violations.iterator().next().getMessage());
    }

    @Test
    void bankStatementRequest_ShouldFailWhenMonthIsNull() {
        BankStatementRequest request = new BankStatementRequest(2025, null, "1234567", "1234");

        Set<ConstraintViolation<BankStatementRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("Month is required", violations.iterator().next().getMessage());
    }

    @Test
    void bankStatementRequest_ShouldFailWhenMonthIsInvalid() {
        BankStatementRequest request = new BankStatementRequest(2025, 13, "1234567", "1234");

        Set<ConstraintViolation<BankStatementRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("Invalid month", violations.iterator().next().getMessage());
    }

    @Test
    void bankStatementRequest_ShouldFailWhenAccountNumberIsBlank() {
        BankStatementRequest request = new BankStatementRequest(2025, 3, "", "1234");

        Set<ConstraintViolation<BankStatementRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("Account number is required", violations.iterator().next().getMessage());
    }

    @Test
    void bankStatementRequest_ShouldFailWhenPinIsBlank() {
        BankStatementRequest request = new BankStatementRequest(2025, 3, "1234567", "");

        Set<ConstraintViolation<BankStatementRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("PIN is required", violations.iterator().next().getMessage());
    }
}