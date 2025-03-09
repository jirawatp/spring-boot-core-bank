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
}