package com.pattanayutanachot.jirawat.core.bank.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleAccountNotFoundException() {
        AccountNotFoundException exception = new AccountNotFoundException("Account not found");
        ResponseEntity<Map<String, String>> response = handler.handleAccountNotFoundException(exception);

        assertEquals(404, response.getStatusCode().value());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void handleTransactionFailedException() {
        TransactionFailedException exception = new TransactionFailedException("Transaction failed");
        ResponseEntity<Map<String, String>> response = handler.handleTransactionFailedException(exception);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().containsKey("error"));
    }
}