package com.pattanayutanachot.jirawat.core.bank.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleAccountNotFoundException() {
        AccountNotFoundException exception = new AccountNotFoundException("Account not found");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleAccountNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Account not found", response.getBody().get("error"));
    }

    @Test
    void shouldHandleTransactionFailedException() {
        TransactionFailedException exception = new TransactionFailedException("Transaction failed");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleTransactionFailedException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Transaction failed", response.getBody().get("error"));
    }

    @Test
    void shouldHandleValidationExceptions() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("object", "field1", "Field1 is required"),
                new FieldError("object", "field2", "Field2 must be a valid number")
        ));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Field1 is required", response.getBody().get("field1"));
        assertEquals("Field2 must be a valid number", response.getBody().get("field2"));
    }

    @Test
    void shouldHandleGenericException() {
        Exception exception = new Exception("Unexpected error occurred");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("error").contains("Unexpected error occurred"));
    }
}