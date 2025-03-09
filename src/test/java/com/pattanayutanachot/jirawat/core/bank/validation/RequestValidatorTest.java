package com.pattanayutanachot.jirawat.core.bank.validation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RequestValidatorTest {

    @Test
    void testIsValidAccountNumber_ShouldReturnTrue_ForValidAccountNumber() {
        assertTrue(RequestValidator.isValidAccountNumber("1234567890"));
    }

    @Test
    void testIsValidAccountNumber_ShouldReturnFalse_ForInvalidAccountNumber() {
        assertFalse(RequestValidator.isValidAccountNumber("12345"));
        assertFalse(RequestValidator.isValidAccountNumber("12345678901"));
        assertFalse(RequestValidator.isValidAccountNumber("ABCDEFGHIJ"));
        assertFalse(RequestValidator.isValidAccountNumber("12345abcde"));
        assertFalse(RequestValidator.isValidAccountNumber(null));
    }

    @Test
    void testIsValidPin_ShouldReturnTrue_ForValidPin() {
        assertTrue(RequestValidator.isValidPin("123456"));
    }

    @Test
    void testIsValidPin_ShouldReturnFalse_ForInvalidPin() {
        assertFalse(RequestValidator.isValidPin("12345"));
        assertFalse(RequestValidator.isValidPin("1234567"));
        assertFalse(RequestValidator.isValidPin("abcdef"));
        assertFalse(RequestValidator.isValidPin("1234ab"));
        assertFalse(RequestValidator.isValidPin(null));
    }
}