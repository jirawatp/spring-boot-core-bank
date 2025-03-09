package com.pattanayutanachot.jirawat.core.bank.dto;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TransferRequestTest {

    private final Validator validator;

    TransferRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void transferRequest_ShouldBeValid() {
        TransferRequest request = TransferRequest.builder()
                .fromAccountNumber("123456789")
                .toAccountNumber("987654321")
                .amount(new BigDecimal("100"))
                .pin("123456")
                .build();

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void transferRequest_ShouldFail_WhenFromAccountNumberIsBlank() {
        TransferRequest request = TransferRequest.builder()
                .fromAccountNumber("")
                .toAccountNumber("987654321")
                .amount(new BigDecimal("100"))
                .pin("123456")
                .build();

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void transferRequest_ShouldFail_WhenToAccountNumberIsBlank() {
        TransferRequest request = TransferRequest.builder()
                .fromAccountNumber("123456789")
                .toAccountNumber("")
                .amount(new BigDecimal("100"))
                .pin("123456")
                .build();

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void transferRequest_ShouldFail_WhenAmountIsLessThanOne() {
        TransferRequest request = TransferRequest.builder()
                .fromAccountNumber("123456789")
                .toAccountNumber("987654321")
                .amount(new BigDecimal("0"))
                .pin("123456")
                .build();

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void transferRequest_ShouldFail_WhenPinIsBlank() {
        TransferRequest request = TransferRequest.builder()
                .fromAccountNumber("123456789")
                .toAccountNumber("987654321")
                .amount(new BigDecimal("100"))
                .pin("")
                .build();

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
}