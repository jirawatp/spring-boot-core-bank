package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.exception.AccountNotFoundException;
import com.pattanayutanachot.jirawat.core.bank.exception.TransactionFailedException;
import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.model.Transaction;
import com.pattanayutanachot.jirawat.core.bank.repository.AccountRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account testAccount;
    private Account recipientAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setBalance(new BigDecimal("1000.00"));

        recipientAccount = new Account();
        recipientAccount.setId(2L);
        recipientAccount.setBalance(new BigDecimal("500.00"));

        testTransaction = new Transaction();
        testTransaction.setAccount(testAccount);
        testTransaction.setAmount(new BigDecimal("200.00"));
        testTransaction.setType("DEPOSIT");
    }

    @Test
    void testGetAllTransactions() {
        when(transactionRepository.findAll()).thenReturn(List.of(testTransaction));

        List<Transaction> transactions = transactionService.getAllTransactions();

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testGetTransactionById_ExistingTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        Transaction foundTransaction = transactionService.getTransactionById(1L);

        assertNotNull(foundTransaction);
        assertEquals(testTransaction.getAmount(), foundTransaction.getAmount());
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTransactionById_TransactionNotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TransactionFailedException.class, () -> transactionService.getTransactionById(1L));
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void testDeposit_Success() {
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionService.deposit(testTransaction);

        assertEquals(new BigDecimal("1200.00"), testAccount.getBalance());
        verify(accountRepository, times(1)).save(testAccount);
        verify(transactionRepository, times(1)).save(testTransaction);
    }

    @Test
    void testDeposit_AccountNotFound() {
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> transactionService.deposit(testTransaction));
        verify(accountRepository, times(1)).findById(testAccount.getId());
    }

    @Test
    void testWithdraw_Success() {
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        testTransaction.setType("WITHDRAW");
        testTransaction.setAmount(new BigDecimal("200.00"));

        Transaction result = transactionService.withdraw(testTransaction);

        assertEquals(new BigDecimal("800.00"), testAccount.getBalance());
        verify(accountRepository, times(1)).save(testAccount);
        verify(transactionRepository, times(1)).save(testTransaction);
    }

    @Test
    void testWithdraw_InsufficientFunds() {
        testTransaction.setType("WITHDRAW");
        testTransaction.setAmount(new BigDecimal("2000.00"));

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

        assertThrows(TransactionFailedException.class, () -> transactionService.withdraw(testTransaction));
        verify(accountRepository, times(1)).findById(testAccount.getId());
    }

    @Test
    void testWithdraw_AccountNotFound() {
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> transactionService.withdraw(testTransaction));
        verify(accountRepository, times(1)).findById(testAccount.getId());
    }

    @Test
    void testTransfer_Success() {
        BigDecimal transferAmount = new BigDecimal("200.00");

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(accountRepository.findById(recipientAccount.getId())).thenReturn(Optional.of(recipientAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionService.transfer(testAccount.getId(), recipientAccount.getId(), transferAmount);

        assertEquals(new BigDecimal("800.00"), testAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), recipientAccount.getBalance());

        verify(accountRepository, times(1)).save(testAccount);
        verify(accountRepository, times(1)).save(recipientAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testTransfer_InsufficientFunds() {
        BigDecimal transferAmount = new BigDecimal("2000.00");

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(accountRepository.findById(recipientAccount.getId())).thenReturn(Optional.of(recipientAccount));

        assertThrows(TransactionFailedException.class, () -> transactionService.transfer(testAccount.getId(), recipientAccount.getId(), transferAmount));

        verify(accountRepository, times(1)).findById(testAccount.getId());
        verify(accountRepository, times(1)).findById(recipientAccount.getId());
    }

    @Test
    void testTransfer_SenderAccountNotFound() {
        BigDecimal transferAmount = new BigDecimal("200.00");

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> transactionService.transfer(testAccount.getId(), recipientAccount.getId(), transferAmount));

        verify(accountRepository, times(1)).findById(testAccount.getId());
    }

    @Test
    void testTransfer_RecipientAccountNotFound() {
        BigDecimal transferAmount = new BigDecimal("200.00");

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(accountRepository.findById(recipientAccount.getId())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> transactionService.transfer(testAccount.getId(), recipientAccount.getId(), transferAmount));

        verify(accountRepository, times(1)).findById(testAccount.getId());
        verify(accountRepository, times(1)).findById(recipientAccount.getId());
    }
}