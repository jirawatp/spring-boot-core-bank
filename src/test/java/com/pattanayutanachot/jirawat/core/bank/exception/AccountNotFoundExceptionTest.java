package com.pattanayutanachot.jirawat.core.bank.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String errorMessage = "Account not found with ID 12345";
        AccountNotFoundException exception = new AccountNotFoundException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void shouldThrowAndCatchException() {
        String errorMessage = "Account not found";
        try {
            throw new AccountNotFoundException(errorMessage);
        } catch (AccountNotFoundException ex) {
            assertEquals(errorMessage, ex.getMessage());
        }
    }
}