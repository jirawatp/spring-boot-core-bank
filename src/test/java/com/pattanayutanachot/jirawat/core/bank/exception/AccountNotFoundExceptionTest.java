package com.pattanayutanachot.jirawat.core.bank.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String errorMessage = "Account not found with ID 123";
        AccountNotFoundException exception = new AccountNotFoundException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testExceptionType() {
        AccountNotFoundException exception = new AccountNotFoundException("Test message");

        assertTrue(exception instanceof RuntimeException);
    }
}