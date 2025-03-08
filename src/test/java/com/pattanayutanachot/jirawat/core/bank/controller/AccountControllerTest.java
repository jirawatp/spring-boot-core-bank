package com.pattanayutanachot.jirawat.core.bank.controller;

import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.service.AccountService;
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

class AccountControllerTest {

    private AccountService accountService;
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        accountService = Mockito.mock(AccountService.class);
        accountController = new AccountController(accountService);
    }

    @Test
    void getAllAccounts() {
        Account account = new Account(1L, "1234567890", "1234567890123", "สมชาย ใจดี", "Somchai Jaidee",
                "somchai@example.com", "password123", "123456", BigDecimal.valueOf(1000.0), LocalDateTime.now());

        List<Account> accounts = Arrays.asList(account);
        when(accountService.getAllAccounts()).thenReturn(accounts);

        ResponseEntity<List<Account>> response = accountController.getAllAccounts();

        assertEquals(1, response.getBody().size());
        assertEquals("1234567890", response.getBody().get(0).getAccountNumber());
    }

    @Test
    void getAccountById() {
        Account account = new Account(1L, "1234567890", "1234567890123", "สมชาย ใจดี", "Somchai Jaidee",
                "somchai@example.com", "password123", "123456", BigDecimal.valueOf(1000.0), LocalDateTime.now());

        when(accountService.getAccountById(1L)).thenReturn(account);

        ResponseEntity<Account> response = accountController.getAccountById(1L);

        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void createAccount() {
        Account account = new Account(null, "1234567890", "1234567890123", "สมชาย ใจดี", "Somchai Jaidee",
                "somchai@example.com", "password123", "123456", BigDecimal.valueOf(1000.0), LocalDateTime.now());

        when(accountService.createAccount(account)).thenReturn(account);

        ResponseEntity<Account> response = accountController.createAccount(account);

        assertEquals("1234567890", response.getBody().getAccountNumber());
    }

    @Test
    void updateAccount() {
        Account updatedAccount = new Account(1L, "1234567890", "1234567890123", "สมชาย ใหม่", "Somchai New",
                "somchai@example.com", "password123", "123456", BigDecimal.valueOf(2000.0), LocalDateTime.now());

        when(accountService.updateAccount(1L, updatedAccount)).thenReturn(updatedAccount);

        ResponseEntity<Account> response = accountController.updateAccount(1L, updatedAccount);

        assertEquals("สมชาย ใหม่", response.getBody().getThaiName());
    }

    @Test
    void deleteAccount() {
        accountController.deleteAccount(1L);
        verify(accountService).deleteAccount(1L);
    }
}