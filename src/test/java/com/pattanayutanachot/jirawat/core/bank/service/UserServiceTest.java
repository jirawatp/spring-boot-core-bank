package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.RegisterRequest;
import com.pattanayutanachot.jirawat.core.bank.model.Role;
import com.pattanayutanachot.jirawat.core.bank.model.RoleType;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.repository.RoleRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private Role customerRole;
    private Role tellerRole;
    private User adminUser;
    private User newUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registerRequest = new RegisterRequest("customer@example.com", "password123");

        customerRole = new Role(1L, RoleType.CUSTOMER);
        tellerRole = new Role(2L, RoleType.TELLER);

        adminUser = User.builder()
                .id(1L)
                .email("admin@example.com")
                .password("adminpass")
                .roles(Collections.singleton(new Role(3L, RoleType.SUPER_ADMIN)))
                .build();

        newUser = User.builder()
                .id(2L)
                .email(registerRequest.email())
                .password("encodedPassword")
                .roles(Collections.singleton(customerRole))
                .build();
    }

    @Test
    void registerCustomer_ShouldRegisterCustomer_WhenValidRequest() {
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(roleRepository.findByName(RoleType.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User registeredUser = userService.registerCustomer(registerRequest);

        assertNotNull(registeredUser);
        assertEquals(registerRequest.email(), registeredUser.getEmail());
        assertEquals(1, registeredUser.getRoles().size());
        assertTrue(registeredUser.getRoles().stream().anyMatch(role -> role.getName() == RoleType.CUSTOMER));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerCustomer_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.registerCustomer(registerRequest)
        );

        assertEquals("Email is already registered.", exception.getMessage());
    }

    @Test
    void registerTeller_ShouldThrowException_WhenNonSuperAdminTriesToRegister() {
        User nonAdminUser = User.builder()
                .id(2L)
                .email("user@example.com")
                .password("password")
                .roles(Collections.singleton(new Role(4L, RoleType.CUSTOMER)))
                .build();

        RegisterRequest tellerRequest = new RegisterRequest("teller@example.com", "password123");

        Exception exception = assertThrows(SecurityException.class, () ->
                userService.registerTeller(tellerRequest, nonAdminUser)
        );

        assertEquals("Only SUPER_ADMIN can register TELLER accounts.", exception.getMessage());
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenEmailExists() {
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(newUser));

        Optional<User> userOptional = userService.getUserByEmail("customer@example.com");

        assertTrue(userOptional.isPresent());
        assertEquals("customer@example.com", userOptional.get().getEmail());
    }

    @Test
    void getUserByEmail_ShouldReturnEmpty_WhenEmailNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Optional<User> userOptional = userService.getUserByEmail("unknown@example.com");

        assertFalse(userOptional.isPresent());
    }
}