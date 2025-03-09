package com.pattanayutanachot.jirawat.core.bank.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void shouldCreateAccountWithDefaultValues() {
        Account account = new Account();

        assertNotNull(account);
        assertNull(account.getId());
        assertNull(account.getAccountNumber());
        assertNotNull(account.getBalance());
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertNull(account.getUser());
        assertNull(account.getCreatedByTeller());
        assertNull(account.getCreatedAt());
    }

    @Test
    void shouldCreateAccountWithBuilder() {
        LocalDateTime createdAt = LocalDateTime.now();
        User user = new User();
        User teller = new User();

        Account account = Account.builder()
                .id(1L)
                .accountNumber("1234567")
                .balance(BigDecimal.valueOf(500.00))
                .user(user)
                .createdByTeller(teller)
                .createdAt(createdAt)
                .build();

        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals("1234567", account.getAccountNumber());
        assertEquals(BigDecimal.valueOf(500.00), account.getBalance());
        assertEquals(user, account.getUser());
        assertEquals(teller, account.getCreatedByTeller());
        assertEquals(createdAt, account.getCreatedAt());
    }

    @Test
    void shouldSetCreatedAtBeforePersist() {
        Account account = new Account();
        account.onCreate();

        assertNotNull(account.getCreatedAt());
    }
}