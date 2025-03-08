package com.pattanayutanachot.jirawat.core.bank.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionFailedExceptionTest {

    @Test
    void testTransactionFailedExceptionMessage() {
        String expectedMessage = "Transaction failed due to insufficient balance";
        TransactionFailedException exception = new TransactionFailedException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}