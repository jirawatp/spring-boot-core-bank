package com.pattanayutanachot.jirawat.core.bank.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFailedExceptionTest {

    @Test
    void shouldCreateTransactionFailedExceptionWithMessage() {
        String errorMessage = "Transaction failed due to insufficient balance";

        TransactionFailedException exception = new TransactionFailedException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }
}