package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.*;
import com.pattanayutanachot.jirawat.core.bank.model.Role;
import com.pattanayutanachot.jirawat.core.bank.model.RoleType;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.repository.RoleRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import com.pattanayutanachot.jirawat.core.bank.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate; // Redis integration

    /**
     * Registers a new CUSTOMER user.
     */
    @Transactional
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered.");
        }

        Role customerRole = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found."));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Collections.singleton(customerRole))
                .build();

        userRepository.save(user);
        return "User registered successfully as CUSTOMER.";
    }

    /**
     * Registers a new TELLER, only allowed for SUPER_ADMIN users.
     */
    @Transactional
    public String registerTeller(RegisterRequest request, Long adminUserId) {
        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found."));

        boolean isSuperAdmin = adminUser.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleType.SUPER_ADMIN);

        if (!isSuperAdmin) {
            throw new RuntimeException("Only SUPER_ADMIN can register a TELLER.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered.");
        }

        Role tellerRole = roleRepository.findByName(RoleType.TELLER)
                .orElseThrow(() -> new RuntimeException("Role TELLER not found."));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Collections.singleton(tellerRole))
                .build();

        userRepository.save(user);
        return "User registered successfully as TELLER.";
    }

    /**
     * Completes the profile for the currently authenticated user.
     */
    @Transactional
    public String completeProfile(Long userId, CompleteProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        // Reject if profile is already completed
        if (user.getCitizenId() != null && user.getThaiName() != null && user.getEnglishName() != null) {
            throw new RuntimeException("Profile already completed.");
        }

        user.setCitizenId(request.citizenId());
        user.setThaiName(request.thaiName());
        user.setEnglishName(request.englishName());
        user.setPin(request.pin());

        userRepository.save(user);
        return "Profile completed successfully.";
    }

    /**
     * Authenticates user and returns JWT, storing it in Redis.
     */
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found."));

        String token = jwtUtil.generateToken(user);

        // Store JWT in Redis with expiration
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("jwt:" + user.getId(), token, Duration.ofHours(2));

        return new LoginResponse(token, user.getEmail());
    }

    /**
     * Logs out user by removing JWT from Redis.
     */
    public void logout(Long userId) {
        redisTemplate.delete("jwt:" + userId);
    }

    /**
     * Validates JWT by checking Redis.
     */
    public boolean isJwtValid(Long userId, String token) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String storedToken = ops.get("jwt:" + userId);
        return token.equals(storedToken);
    }
}