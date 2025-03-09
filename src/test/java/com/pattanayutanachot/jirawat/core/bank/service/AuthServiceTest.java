package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.*;
import com.pattanayutanachot.jirawat.core.bank.model.Role;
import com.pattanayutanachot.jirawat.core.bank.model.RoleType;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.repository.RoleRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import com.pattanayutanachot.jirawat.core.bank.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private User customerUser;
    private User adminUser;
    private Role customerRole;
    private Role tellerRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customerRole = new Role(1L, RoleType.CUSTOMER);
        tellerRole = new Role(2L, RoleType.TELLER);
        adminRole = new Role(3L, RoleType.SUPER_ADMIN);

        customerUser = User.builder()
                .id(1L)
                .email("customer@example.com")
                .password("encodedPassword")
                .roles(Collections.singleton(customerRole))
                .build();

        adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .password("encodedPassword")
                .roles(Collections.singleton(adminRole))
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest("newuser@example.com", "password123");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(roleRepository.findByName(RoleType.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String result = authService.register(request);

        assertEquals("User registered successfully as CUSTOMER. Please complete your profile.", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerTeller_Success() {
        RegisterRequest request = new RegisterRequest("teller@example.com", "password123");

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(roleRepository.findByName(RoleType.TELLER)).thenReturn(Optional.of(tellerRole));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String result = authService.registerTeller(request, adminUser.getId());

        assertEquals("User registered successfully as TELLER.", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void completeProfile_Success() {
        CompleteProfileRequest request = new CompleteProfileRequest("1111111111111", "John Thai", "John Eng", "123456");

        when(userRepository.findById(customerUser.getId())).thenReturn(Optional.of(customerUser));
        when(userRepository.findByCitizenId(request.citizenId())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String result = authService.completeProfile(customerUser.getId(), request);

        assertEquals("Profile completed successfully.", result);
        assertEquals("1111111111111", customerUser.getCitizenId());
        assertEquals("John Thai", customerUser.getThaiName());
        assertEquals("John Eng", customerUser.getEnglishName());
        assertEquals("123456", customerUser.getPin());
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("customer@example.com", "password123");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(customerUser));
        when(jwtUtil.generateToken(customerUser)).thenReturn("mockedToken");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockedToken", response.token());
        verify(valueOperations, times(1)).set(anyString(), eq("mockedToken"), any());
    }

    @Test
    void logout_Success() {
        authService.logout(customerUser.getId());

        verify(redisTemplate, times(1)).delete("jwt:" + customerUser.getId());
    }
}