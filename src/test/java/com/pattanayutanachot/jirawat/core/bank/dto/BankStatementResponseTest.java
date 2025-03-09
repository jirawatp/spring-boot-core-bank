package com.pattanayutanachot.jirawat.core.bank.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankStatementResponseTest {

    @Test
    void bankStatementResponse_ShouldCreateSuccessfully() {
        BankStatementResponse response = BankStatementResponse.builder()
                .accountNumber("1234567")
                .date("2025-03-09")
                .time("14:30:00")
                .code("A1")
                .channel("ATM")
                .debit(new BigDecimal("500.00"))
                .credit(null)
                .balance(new BigDecimal("4500.00"))
                .remark("Withdraw from ATM")
                .build();

        assertNotNull(response);
        assertEquals("1234567", response.accountNumber());
        assertEquals("2025-03-09", response.date());
        assertEquals("14:30:00", response.time());
        assertEquals("A1", response.code());
        assertEquals("ATM", response.channel());
        assertEquals(new BigDecimal("500.00"), response.debit());
        assertNull(response.credit());
        assertEquals(new BigDecimal("4500.00"), response.balance());
        assertEquals("Withdraw from ATM", response.remark());
    }

    @Test
    void bankStatementResponse_ShouldSupportCreditTransaction() {
        BankStatementResponse response = BankStatementResponse.builder()
                .accountNumber("9876543")
                .date("2025-03-10")
                .time("10:00:00")
                .code("A2")
                .channel("Bank Transfer")
                .debit(null)
                .credit(new BigDecimal("1000.00"))
                .balance(new BigDecimal("5500.00"))
                .remark("Receive from x4567 John Doe")
                .build();

        assertNotNull(response);
        assertEquals("9876543", response.accountNumber());
        assertEquals("2025-03-10", response.date());
        assertEquals("10:00:00", response.time());
        assertEquals("A2", response.code());
        assertEquals("Bank Transfer", response.channel());
        assertNull(response.debit());
        assertEquals(new BigDecimal("1000.00"), response.credit());
        assertEquals(new BigDecimal("5500.00"), response.balance());
        assertEquals("Receive from x4567 John Doe", response.remark());
    }
}