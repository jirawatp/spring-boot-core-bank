package com.pattanayutanachot.jirawat.core.bank.controller;

import com.pattanayutanachot.jirawat.core.bank.dto.*;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.service.AuthService;
import com.pattanayutanachot.jirawat.core.bank.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

class AuthControllerTest {

    private MockMvc mockMvc;
    private AuthService authService;
    private UserService userService;
    private UserDetails mockUserDetails;
    private User mockUser;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        userService = mock(UserService.class);
        AuthController authController = new AuthController(authService, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        mockUserDetails = mock(UserDetails.class);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
    }

    @Test
    void register_ShouldReturnSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("test@example.com", "password");
        when(authService.register(any(RegisterRequest.class))).thenReturn("Registration successful.");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration successful."));
    }

    @Test
    void registerTeller_ShouldReturnSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("teller@example.com", "password");
        when(mockUserDetails.getUsername()).thenReturn("admin@example.com");
        when(userService.getUserByEmail("admin@example.com")).thenReturn(Optional.of(mockUser));
        when(authService.registerTeller(any(RegisterRequest.class), anyLong())).thenReturn("Teller registered successfully.");

        mockMvc.perform(post("/api/auth/register-teller")
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"teller@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Teller registered successfully."));
    }

    @Test
    void completeProfile_ShouldReturnSuccess() throws Exception {
        CompleteProfileRequest request = new CompleteProfileRequest("1234567890", "John Doe", "John", "1234");
        when(mockUserDetails.getUsername()).thenReturn("customer@example.com");
        when(userService.getUserByEmail("customer@example.com")).thenReturn(Optional.of(mockUser));
        when(authService.completeProfile(anyLong(), any(CompleteProfileRequest.class))).thenReturn("Profile completed successfully.");

        mockMvc.perform(post("/api/auth/complete-profile")
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"citizenId\":\"1234567890\", \"thaiName\":\"John Doe\",\"englishName\":\"John\",\"pin\":\"1234\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile completed successfully."));
    }

    @Test
    void login_ShouldReturnJwtToken() throws Exception {
        LoginRequest request = new LoginRequest("customer@example.com", "password");
        LoginResponse response = new LoginResponse("mock-jwt-token", "customer@example.com");
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"customer@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void logout_ShouldReturnSuccess() throws Exception {
        when(mockUserDetails.getUsername()).thenReturn("customer@example.com");
        when(userService.getUserByEmail("customer@example.com")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/api/auth/logout")
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged out successfully."));

        verify(authService, times(1)).logout(mockUser.getId());
    }
}