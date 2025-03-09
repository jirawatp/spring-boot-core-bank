package com.pattanayutanachot.jirawat.core.bank.controller;

import com.pattanayutanachot.jirawat.core.bank.dto.*;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.service.AuthService;
import com.pattanayutanachot.jirawat.core.bank.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // Requires authentication
public class AuthController {

    private final AuthService authService;
    private final UserService userService; // ✅ Use UserService to fetch full User entity

    /**
     * Register a new CUSTOMER user.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Register a new TELLER (Only for SUPER_ADMIN).
     */
    @PostMapping("/register-teller")
    @PreAuthorize("hasRole('SUPER_ADMIN')") // Only Super Admin can register Tellers
    public ResponseEntity<String> registerTeller(@AuthenticationPrincipal UserDetails adminDetails,
                                                 @Valid @RequestBody RegisterRequest request) {
        User adminUser = userService.getUserByEmail(adminDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + adminDetails.getUsername()));
        return ResponseEntity.ok(authService.registerTeller(request, adminUser.getId()));
    }

    /**
     * Complete profile (Only for authenticated users).
     */
    @PostMapping("/complete-profile")
    @PreAuthorize("isAuthenticated()") // Ensure the user is logged in
    public ResponseEntity<String> completeProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                  @Valid @RequestBody CompleteProfileRequest request) {
        User user = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userDetails.getUsername()));
        return ResponseEntity.ok(authService.completeProfile(user.getId(), request));
    }

    /**
     * Login and receive JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Logout and remove JWT token from Redis.
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userDetails.getUsername()));
        authService.logout(user.getId());
        return ResponseEntity.ok("User logged out successfully.");
    }
}