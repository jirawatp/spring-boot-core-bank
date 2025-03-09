package com.pattanayutanachot.jirawat.core.bank.dto;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VerifyTransferRequestTest {

    private final Validator validator;

    VerifyTransferRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void verifyTransferRequest_ShouldBeValid() {
        VerifyTransferRequest request = VerifyTransferRequest.builder()
                .withdrawalAccountNumber("123456789")
                .recipientAccountNumber("987654321")
                .amount(new BigDecimal("100"))
                .build();

        Set<ConstraintViolation<VerifyTransferRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void verifyTransferRequest_ShouldFail_WhenWithdrawalAccountNumberIsBlank() {
        VerifyTransferRequest request = VerifyTransferRequest.builder()
                .withdrawalAccountNumber("")
                .recipientAccountNumber("987654321")
                .amount(new BigDecimal("100"))
                .build();

        Set<ConstraintViolation<VerifyTransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void verifyTransferRequest_ShouldFail_WhenRecipientAccountNumberIsBlank() {
        VerifyTransferRequest request = VerifyTransferRequest.builder()
                .withdrawalAccountNumber("123456789")
                .recipientAccountNumber("")
                .amount(new BigDecimal("100"))
                .build();

        Set<ConstraintViolation<VerifyTransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void verifyTransferRequest_ShouldFail_WhenAmountIsNull() {
        VerifyTransferRequest request = VerifyTransferRequest.builder()
                .withdrawalAccountNumber("123456789")
                .recipientAccountNumber("987654321")
                .amount(null)
                .build();

        Set<ConstraintViolation<VerifyTransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void verifyTransferRequest_ShouldFail_WhenAmountIsLessThanOne() {
        VerifyTransferRequest request = VerifyTransferRequest.builder()
                .withdrawalAccountNumber("123456789")
                .recipientAccountNumber("987654321")
                .amount(new BigDecimal("0"))
                .build();

        Set<ConstraintViolation<VerifyTransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
}