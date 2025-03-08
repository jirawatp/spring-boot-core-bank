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

class AccountTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidAccount() {
        Account account = new Account(
                1L,
                "1234567890",
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                "somchai@example.com",
                "securePassword",
                "123456",
                BigDecimal.valueOf(1000.00),
                LocalDateTime.now()
        );

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void testBlankAccountNumber() {
        Account account = new Account();
        account.setAccountNumber("");
        account.setCitizenId("1234567890123");
        account.setThaiName("สมชาย ใจดี");
        account.setEnglishName("Somchai Jaidee");
        account.setEmail("somchai@example.com");
        account.setPassword("securePassword");
        account.setPin("123456");
        account.setBalance(BigDecimal.valueOf(1000.00));
        account.setCreatedAt(LocalDateTime.now());

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty(), "Expected validation error for blank account number");
    }

    @Test
    void testInvalidEmailFormat() {
        Account account = new Account(
                1L,
                "1234567890",
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                "invalid-email", // Invalid email
                "securePassword",
                "123456",
                BigDecimal.valueOf(1000.00),
                LocalDateTime.now()
        );

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty(), "Expected validation error for invalid email");
    }

    @Test
    void testNegativeBalance() {
        Account account = new Account(
                1L,
                "1234567890",
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                "somchai@example.com",
                "securePassword",
                "123456",
                BigDecimal.valueOf(-50.00), // Negative balance
                LocalDateTime.now()
        );

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty(), "Expected validation error for negative balance");
    }

    @Test
    void testBlankPassword() {
        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setCitizenId("1234567890123");
        account.setThaiName("สมชาย ใจดี");
        account.setEnglishName("Somchai Jaidee");
        account.setEmail("somchai@example.com");
        account.setPassword(""); // Blank password
        account.setPin("123456");
        account.setBalance(BigDecimal.valueOf(1000.00));
        account.setCreatedAt(LocalDateTime.now());

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty(), "Expected validation error for blank password");
    }
}