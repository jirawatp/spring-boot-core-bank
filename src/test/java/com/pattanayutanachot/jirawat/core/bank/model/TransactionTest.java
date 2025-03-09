package com.pattanayutanachot.jirawat.core.bank.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Transaction transaction;
    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(1L)
                .accountNumber("1234567")
                .balance(new BigDecimal("1000.00"))
                .createdAt(LocalDateTime.now())
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("500.00"))
                .channel(TransactionChannel.OTC)
                .remark("Deposit Terminal TELLER123")
                .balanceAfter(new BigDecimal("1500.00"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        assertNotNull(transaction);
        assertEquals(1L, transaction.getId());
        assertEquals(account, transaction.getAccount());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(new BigDecimal("500.00"), transaction.getAmount());
        assertEquals(TransactionChannel.OTC, transaction.getChannel());
        assertEquals("Deposit Terminal TELLER123", transaction.getRemark());
        assertEquals(new BigDecimal("1500.00"), transaction.getBalanceAfter());
        assertNotNull(transaction.getCreatedAt());
    }

    @Test
    void shouldSetCreatedAtBeforePersist() {
        Transaction newTransaction = new Transaction();
        newTransaction.onCreate();
        assertNotNull(newTransaction.getCreatedAt());
    }

    @Test
    void shouldAllowNullableRemark() {
        transaction.setRemark(null);
        assertNull(transaction.getRemark());
    }
}