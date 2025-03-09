package com.pattanayutanachot.jirawat.core.bank.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountResponseTest {

    @Test
    void accountResponse_ShouldStoreValuesCorrectly() {
        String accountNumber = "1234567";
        BigDecimal balance = BigDecimal.valueOf(1500.00);
        LocalDateTime createdAt = LocalDateTime.now();

        AccountResponse accountResponse = new AccountResponse(accountNumber, balance, createdAt);

        assertEquals(accountNumber, accountResponse.accountNumber());
        assertEquals(balance, accountResponse.balance());
        assertEquals(createdAt, accountResponse.createdAt());
    }

    @Test
    void accountResponse_ShouldHandleNullValues() {
        AccountResponse accountResponse = new AccountResponse(null, null, null);

        assertNull(accountResponse.accountNumber());
        assertNull(accountResponse.balance());
        assertNull(accountResponse.createdAt());
    }
}