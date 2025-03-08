package com.pattanayutanachot.jirawat.core.bank.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidatorTest {

    @Test
    void testValidAccountNumber() {
        assertTrue(RequestValidator.isValidAccountNumber("1234567890")); // Valid 10-digit number
    }

    @Test
    void testInvalidAccountNumber() {
        assertFalse(RequestValidator.isValidAccountNumber(null)); // Null input
        assertFalse(RequestValidator.isValidAccountNumber("")); // Empty input
        assertFalse(RequestValidator.isValidAccountNumber("123456789")); // Less than 10 digits
        assertFalse(RequestValidator.isValidAccountNumber("12345678901")); // More than 10 digits
        assertFalse(RequestValidator.isValidAccountNumber("123456789A")); // Contains non-numeric characters
    }

    @Test
    void testValidPin() {
        assertTrue(RequestValidator.isValidPin("123456")); // Valid 6-digit PIN
    }

    @Test
    void testInvalidPin() {
        assertFalse(RequestValidator.isValidPin(null)); // Null input
        assertFalse(RequestValidator.isValidPin("")); // Empty input
        assertFalse(RequestValidator.isValidPin("12345")); // Less than 6 digits
        assertFalse(RequestValidator.isValidPin("1234567")); // More than 6 digits
        assertFalse(RequestValidator.isValidPin("12345A")); // Contains non-numeric characters
    }
}