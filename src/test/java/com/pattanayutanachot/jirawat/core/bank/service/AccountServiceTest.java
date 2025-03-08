package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.exception.AccountNotFoundException;
import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account(
                1L,
                "1234567890",
                "1234567890123",
                "สมชาย ใจดี",
                "Somchai Jaidee",
                "somchai@example.com",
                "securePassword",
                "123456",
                BigDecimal.valueOf(5000.00),
                LocalDateTime.now()
        );
    }

    @Test
    void testGetAllAccounts() {
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> result = accountService.getAllAccounts();

        assertEquals(1, result.size());
        assertEquals(testAccount, result.get(0));
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testGetAccountById_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        Account result = accountService.getAccountById(1L);

        assertNotNull(result);
        assertEquals("Somchai Jaidee", result.getEnglishName());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAccountById_NotFound() {
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById(2L));

        verify(accountRepository, times(1)).findById(2L);
    }

    @Test
    void testCreateAccount() {
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account result = accountService.createAccount(testAccount);

        assertNotNull(result);
        assertEquals("1234567890", result.getAccountNumber());
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void testUpdateAccount() {
        Account updatedDetails = new Account(
                1L,
                "1234567890",
                "1234567890123",
                "สมหญิง ใจดี",
                "Somying Jaidee",
                "somying@example.com",
                "securePassword",
                "123456",
                BigDecimal.valueOf(7000.00),
                LocalDateTime.now()
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedDetails);

        Account result = accountService.updateAccount(1L, updatedDetails);

        assertEquals("Somying Jaidee", result.getEnglishName());
        assertEquals("somying@example.com", result.getEmail());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void testDeleteAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        doNothing().when(accountRepository).delete(testAccount);

        accountService.deleteAccount(1L);

        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).delete(testAccount);
    }

    @Test
    void testDeleteAccount_NotFound() {
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount(2L));

        verify(accountRepository, times(1)).findById(2L);
        verify(accountRepository, times(0)).delete(any(Account.class));
    }
}