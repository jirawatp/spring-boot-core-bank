package com.pattanayutanachot.jirawat.core.bank.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionResponseTest {

    @Test
    void transactionResponse_ShouldCreateValidObject() {
        LocalDateTime timestamp = LocalDateTime.now();

        TransactionResponse response = TransactionResponse.builder()
                .transactionId(1L)
                .accountNumber("123456789")
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("1000.00"))
                .createdAt(timestamp)
                .build();

        assertNotNull(response);
        assertEquals(1L, response.transactionId());
        assertEquals("123456789", response.accountNumber());
        assertEquals("DEPOSIT", response.transactionType());
        assertEquals(new BigDecimal("1000.00"), response.amount());
        assertEquals(timestamp, response.createdAt());
    }

    @Test
    void transactionResponse_ShouldAllowNullValues() {
        TransactionResponse response = TransactionResponse.builder().build();

        assertNotNull(response);
        assertNull(response.transactionId());
        assertNull(response.accountNumber());
        assertNull(response.transactionType());
        assertNull(response.amount());
        assertNull(response.createdAt());
    }
}