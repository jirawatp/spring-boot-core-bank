package com.pattanayutanachot.jirawat.core.bank.controller;

import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.model.Transaction;
import com.pattanayutanachot.jirawat.core.bank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class TransactionControllerTest {

    private TransactionService transactionService;
    private TransactionController transactionController;
    private Account mockAccount;

    @BeforeEach
    void setUp() {
        transactionService = Mockito.mock(TransactionService.class);
        transactionController = new TransactionController(transactionService);
        mockAccount = new Account(1L, "1234567890", "1234567890123", "สมชาย ใจดี", "Somchai Jaidee",
                "somchai@example.com", "password123", "123456", BigDecimal.valueOf(5000.0), LocalDateTime.now());
    }

    @Test
    void getAllTransactions() {
        Transaction transaction = new Transaction(1L, mockAccount, "DEPOSIT", BigDecimal.valueOf(500), LocalDateTime.now());
        List<Transaction> transactions = Arrays.asList(transaction);

        when(transactionService.getAllTransactions()).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions();

        assertEquals(1, response.getBody().size());
        assertEquals("DEPOSIT", response.getBody().get(0).getType());
    }

    @Test
    void getTransactionById() {
        Transaction transaction = new Transaction(1L, mockAccount, "DEPOSIT", BigDecimal.valueOf(500), LocalDateTime.now());

        when(transactionService.getTransactionById(1L)).thenReturn(transaction);

        ResponseEntity<Transaction> response = transactionController.getTransactionById(1L);

        assertEquals("DEPOSIT", response.getBody().getType());
    }

    @Test
    void deposit() {
        Transaction depositTransaction = new Transaction(null, mockAccount, "DEPOSIT", BigDecimal.valueOf(500), LocalDateTime.now());

        when(transactionService.deposit(depositTransaction)).thenReturn(depositTransaction);

        ResponseEntity<Transaction> response = transactionController.deposit(depositTransaction);

        assertEquals("DEPOSIT", response.getBody().getType());
        assertEquals(BigDecimal.valueOf(500), response.getBody().getAmount());
    }

    @Test
    void withdraw() {
        Transaction withdrawTransaction = new Transaction(null, mockAccount, "WITHDRAW", BigDecimal.valueOf(200), LocalDateTime.now());

        when(transactionService.withdraw(withdrawTransaction)).thenReturn(withdrawTransaction);

        ResponseEntity<Transaction> response = transactionController.withdraw(withdrawTransaction);

        assertEquals("WITHDRAW", response.getBody().getType());
        assertEquals(BigDecimal.valueOf(200), response.getBody().getAmount());
    }

    @Test
    void transfer() {
        Account toAccount = new Account(2L, "0987654321", "9876543210987", "สมหญิง ใจดี", "Somying Jaidee",
                "somying@example.com", "password123", "654321", BigDecimal.valueOf(3000.0), LocalDateTime.now());

        Transaction transferTransaction = new Transaction(1L, mockAccount, "TRANSFER", BigDecimal.valueOf(1000), LocalDateTime.now());

        when(transactionService.transfer(1L, 2L, BigDecimal.valueOf(1000))).thenReturn(transferTransaction);

        ResponseEntity<Transaction> response = transactionController.transfer(1L, 2L, BigDecimal.valueOf(1000));

        assertEquals("TRANSFER", response.getBody().getType());
        assertEquals(BigDecimal.valueOf(1000), response.getBody().getAmount());
    }
}