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

class DepositRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void depositRequest_ShouldBeValid() {
        DepositRequest request = DepositRequest.builder()
                .accountNumber("1234567")
                .amount(BigDecimal.valueOf(1000))
                .build();

        Set<ConstraintViolation<DepositRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void depositRequest_ShouldFail_WhenAccountNumberIsBlank() {
        DepositRequest request = DepositRequest.builder()
                .accountNumber("")
                .amount(BigDecimal.valueOf(1000))
                .build();

        Set<ConstraintViolation<DepositRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Account number is required", violations.iterator().next().getMessage());
    }

    @Test
    void depositRequest_ShouldFail_WhenAmountIsNull() {
        DepositRequest request = DepositRequest.builder()
                .accountNumber("1234567")
                .amount(null)
                .build();

        Set<ConstraintViolation<DepositRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Amount is required", violations.iterator().next().getMessage());
    }

    @Test
    void depositRequest_ShouldFail_WhenAmountIsLessThanOne() {
        DepositRequest request = DepositRequest.builder()
                .accountNumber("1234567")
                .amount(BigDecimal.ZERO)
                .build();

        Set<ConstraintViolation<DepositRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Deposit amount must be at least 1 THB", violations.iterator().next().getMessage());
    }
}