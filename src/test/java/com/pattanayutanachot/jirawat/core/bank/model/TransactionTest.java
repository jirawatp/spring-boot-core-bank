package com.pattanayutanachot.jirawat.core.bank.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidTransaction() {
        Account account = new Account(
                1L,
                "1234567890",
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                "somchai@example.com",
                "securePassword",
                "123456",
                BigDecimal.valueOf(5000.00),
                LocalDateTime.now()
        );

        Transaction transaction = new Transaction(
                1L,
                account,
                "Deposit",
                BigDecimal.valueOf(100.00),
                LocalDateTime.now()
        );

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void testNullAccount() {
        Transaction transaction = new Transaction(
                1L,
                null, // Null account
                "Deposit",
                BigDecimal.valueOf(100.00),
                LocalDateTime.now()
        );

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty(), "Expected validation error for null account");
    }

    @Test
    void testNullTransactionType() {
        Account account = new Account(
                1L,
                "1234567890",
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                "somchai@example.com",
                "securePassword",
                "123456",
                BigDecimal.valueOf(5000.00),
                LocalDateTime.now()
        );

        Transaction transaction = new Transaction(
                1L,
                account,
                null, // Null transaction type
                BigDecimal.valueOf(100.00),
                LocalDateTime.now()
        );

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty(), "Expected validation error for null transaction type");
    }

    @Test
    void testNegativeTransactionAmount() {
        Account account = new Account(
                1L,
                "1234567890",
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                "somchai@example.com",
                "securePassword",
                "123456",
                BigDecimal.valueOf(5000.00),
                LocalDateTime.now()
        );

        Transaction transaction = new Transaction(
                1L,
                account,
                "Withdraw",
                BigDecimal.valueOf(-50.00), // Negative amount
                LocalDateTime.now()
        );

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty(), "Expected validation error for negative amount");
    }

    @Test
    void testZeroTransactionAmount() {
        Account account = new Account(
                1L,
                "1234567890",
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                "somchai@example.com",
                "securePassword",
                "123456",
                BigDecimal.valueOf(5000.00),
                LocalDateTime.now()
        );

        Transaction transaction = new Transaction(
                1L,
                account,
                "Deposit",
                BigDecimal.ZERO, // Zero amount
                LocalDateTime.now()
        );

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty(), "Expected validation error for zero amount");
    }
}