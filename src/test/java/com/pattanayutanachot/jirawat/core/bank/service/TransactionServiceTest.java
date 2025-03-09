package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.BankStatementRequest;
import com.pattanayutanachot.jirawat.core.bank.dto.BankStatementResponse;
import com.pattanayutanachot.jirawat.core.bank.dto.TransactionResponse;
import com.pattanayutanachot.jirawat.core.bank.model.*;
import com.pattanayutanachot.jirawat.core.bank.repository.AccountRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.TransactionRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User customer;
    private Account account;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = User.builder()
                .id(1L)
                .email("customer@example.com")
                .password("password")
                .pin("123456")
                .build();

        account = Account.builder()
                .id(1L)
                .accountNumber("1234567")
                .balance(new BigDecimal("5000"))
                .user(customer)
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("1000"))
                .channel(TransactionChannel.OTC)
                .balanceAfter(new BigDecimal("6000"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllTransactions_ShouldReturnTransactionList() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        List<TransactionResponse> transactions = transactionService.getAllTransactions();

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals("1234567", transactions.get(0).accountNumber());
        assertEquals(TransactionType.DEPOSIT.name(), transactions.get(0).transactionType());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void getBankStatement_ShouldReturnBankStatement_WhenValidRequest() {
        BankStatementRequest request = new BankStatementRequest(2025, 3, "1234567", "123456");

        when(accountRepository.findByAccountNumber("1234567")).thenReturn(Optional.of(account));
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByAccountAndCreatedAtBetweenOrderByCreatedAtAsc(
                any(), any(), any()))
                .thenReturn(List.of(transaction));

        List<BankStatementResponse> bankStatement = transactionService.getBankStatement(1L, request);

        assertNotNull(bankStatement);
        assertEquals(1, bankStatement.size());
        assertEquals("1234567", bankStatement.get(0).accountNumber());
        assertEquals(new BigDecimal("1000"), bankStatement.get(0).credit());
        verify(transactionRepository, times(1)).findByAccountAndCreatedAtBetweenOrderByCreatedAtAsc(any(), any(), any());
    }

    @Test
    void getBankStatement_ShouldThrowException_WhenAccountNotFound() {
        BankStatementRequest request = new BankStatementRequest(2025, 3, "0000000", "123456");

        when(accountRepository.findByAccountNumber("0000000")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                transactionService.getBankStatement(1L, request)
        );

        assertEquals("Account not found.", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber("0000000");
    }

    @Test
    void getBankStatement_ShouldThrowException_WhenInvalidPIN() {
        BankStatementRequest request = new BankStatementRequest(2025, 3, "1234567", "999999");

        when(accountRepository.findByAccountNumber("1234567")).thenReturn(Optional.of(account));
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));

        Exception exception = assertThrows(RuntimeException.class, () ->
                transactionService.getBankStatement(1L, request)
        );

        assertEquals("Invalid PIN.", exception.getMessage());
    }
}