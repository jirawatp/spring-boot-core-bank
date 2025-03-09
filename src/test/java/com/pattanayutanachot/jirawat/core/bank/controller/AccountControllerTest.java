package com.pattanayutanachot.jirawat.core.bank.controller;

import com.pattanayutanachot.jirawat.core.bank.dto.*;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import com.pattanayutanachot.jirawat.core.bank.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

class AccountControllerTest {

    private MockMvc mockMvc;
    private AccountService accountService;
    private UserRepository userRepository;
    private UserDetails mockUserDetails;
    private User mockUser;

    @BeforeEach
    void setUp() {
        accountService = mock(AccountService.class);
        userRepository = mock(UserRepository.class);
        AccountController accountController = new AccountController(accountService, userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();

        mockUserDetails = mock(UserDetails.class);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
    }

    @Test
    void createAccount_ShouldReturnSuccess() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest("1234567890123", "John Doe", "John", new BigDecimal("500"));
        when(mockUserDetails.getUsername()).thenReturn("teller@example.com");
        when(userRepository.findByEmail("teller@example.com")).thenReturn(Optional.of(mockUser));
        when(accountService.createAccount(any(CreateAccountRequest.class), anyLong())).thenReturn("Account created successfully.");

        mockMvc.perform(post("/api/account/create")
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"citizenId\":\"1234567890123\",\"thaiName\":\"John Doe\",\"englishName\":\"John\",\"initialDeposit\":500}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account created successfully."));
    }

    @Test
    void deposit_ShouldReturnSuccess() throws Exception {
        DepositRequest request = new DepositRequest("1234567", new BigDecimal("1000"));
        when(mockUserDetails.getUsername()).thenReturn("teller@example.com");
        when(userRepository.findByEmail("teller@example.com")).thenReturn(Optional.of(mockUser));
        when(accountService.deposit(any(DepositRequest.class), anyLong())).thenReturn("Deposit successful.");

        mockMvc.perform(post("/api/account/deposit")
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"1234567\",\"amount\":1000}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit successful."));
    }

    @Test
    void getCustomerAccounts_ShouldReturnAccountList() throws Exception {
        AccountResponse response = new AccountResponse("1234567", new BigDecimal("1000"), null);
        when(mockUserDetails.getUsername()).thenReturn("customer@example.com");
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(mockUser));
        when(accountService.getCustomerAccounts(anyLong())).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/account/me")
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567"))
                .andExpect(jsonPath("$[0].balance").value(1000));
    }

    @Test
    void transferMoney_ShouldReturnSuccess() throws Exception {
        TransferRequest request = new TransferRequest("1234567", "7654321", new BigDecimal("500"), "1234");
        when(mockUserDetails.getUsername()).thenReturn("customer@example.com");
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(mockUser));
        when(accountService.transferMoney(anyLong(), any(TransferRequest.class))).thenReturn("Transfer successful.");

        mockMvc.perform(post("/api/account/transfer")
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccountNumber\":\"1234567\",\"toAccountNumber\":\"7654321\",\"amount\":500,\"pin\":\"1234\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful."));
    }

    @Test
    void verifyTransfer_ShouldReturnVerificationResponse() throws Exception {
        VerifyTransferResponse response = new VerifyTransferResponse(true, "Valid transfer", "John Doe", "John");
        when(mockUserDetails.getUsername()).thenReturn("customer@example.com");
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(mockUser));
        when(accountService.verifyTransfer(anyLong(), any(VerifyTransferRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/account/verify-transfer")
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipientAccountNumber\":\"7654321\",\"withdrawalAccountNumber\":\"1234567\",\"amount\":500}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Valid transfer"))
                .andExpect(jsonPath("$.thaiName").value("John Doe"))
                .andExpect(jsonPath("$.englishName").value("John"));
    }
}