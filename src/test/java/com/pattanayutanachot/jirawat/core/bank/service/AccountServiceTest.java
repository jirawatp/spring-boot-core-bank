package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.*;
import com.pattanayutanachot.jirawat.core.bank.model.*;
import com.pattanayutanachot.jirawat.core.bank.repository.AccountRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.TransactionRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private User teller;
    private User customer;
    private Account senderAccount;
    private Account recipientAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        teller = new User();
        teller.setId(1L);
        teller.setEmail("teller@example.com");
        teller.setRoles(Set.of(new Role(1L, RoleType.TELLER)));

        customer = new User();
        customer.setId(2L);
        customer.setEmail("customer@example.com");
        customer.setRoles(Set.of(new Role(2L, RoleType.CUSTOMER)));
        customer.setPin("123456");

        senderAccount = Account.builder()
                .id(10L)
                .accountNumber("1234567")
                .balance(new BigDecimal("5000"))
                .user(customer)
                .build();

        recipientAccount = Account.builder()
                .id(11L)
                .accountNumber("7654321")
                .balance(new BigDecimal("2000"))
                .user(new User(3L, "recipient@example.com", "password", "3333333333333", "Recipient Thai", "Recipient Eng", "654321", Set.of(new Role(2L, RoleType.CUSTOMER))))
                .build();
    }

    @Test
    void createAccount_Success() {
        CreateAccountRequest request = new CreateAccountRequest("1111111111111", "John Doe", "John Doe Eng", new BigDecimal("1000"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(teller));
        when(userRepository.findByCitizenId("1111111111111")).thenReturn(Optional.of(customer));
        when(accountRepository.existsByAccountNumber(any())).thenReturn(false);
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String result = accountService.createAccount(request, 1L);

        assertNotNull(result);
        assertTrue(result.contains("Account created successfully"));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void deposit_Success() {
        DepositRequest request = new DepositRequest("1234567", new BigDecimal("2000"));

        when(accountRepository.findByAccountNumber("1234567")).thenReturn(Optional.of(senderAccount));

        String result = accountService.deposit(request, 1L);

        assertNotNull(result);
        assertTrue(result.contains("Deposit successful"));
        assertEquals(new BigDecimal("7000"), senderAccount.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transferMoney_Success() {
        TransferRequest request = new TransferRequest("1234567", "7654321", new BigDecimal("1000"), "123456");

        when(accountRepository.findByAccountNumber("1234567")).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("7654321")).thenReturn(Optional.of(recipientAccount));

        String result = accountService.transferMoney(2L, request);

        assertNotNull(result);
        assertTrue(result.contains("Transfer successful"));
        assertEquals(new BigDecimal("4000"), senderAccount.getBalance());
        assertEquals(new BigDecimal("3000"), recipientAccount.getBalance());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void transferMoney_InsufficientBalance() {
        TransferRequest request = new TransferRequest("1234567", "7654321", new BigDecimal("6000"), "123456");

        when(accountRepository.findByAccountNumber("1234567")).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("7654321")).thenReturn(Optional.of(recipientAccount));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.transferMoney(2L, request));

        assertEquals("Insufficient balance.", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void verifyTransfer_Success() {
        VerifyTransferRequest request = new VerifyTransferRequest("1234567", "7654321", new BigDecimal("1000"));

        when(accountRepository.findByAccountNumber("1234567")).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("7654321")).thenReturn(Optional.of(recipientAccount));

        VerifyTransferResponse response = accountService.verifyTransfer(2L, request);

        assertTrue(response.canTransfer());
        assertEquals("Transfer can proceed.", response.message());
        assertEquals("Recipient Thai", response.recipientThaiName());
        assertEquals("Recipient Eng", response.recipientEnglishName());
    }

    @Test
    void verifyTransfer_InsufficientBalance() {
        VerifyTransferRequest request = new VerifyTransferRequest("1234567", "7654321", new BigDecimal("6000"));

        when(accountRepository.findByAccountNumber("1234567")).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("7654321")).thenReturn(Optional.of(recipientAccount));

        VerifyTransferResponse response = accountService.verifyTransfer(2L, request);

        assertFalse(response.canTransfer());
        assertEquals("Insufficient balance.", response.message());
    }
}