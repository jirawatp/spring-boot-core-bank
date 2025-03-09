package com.pattanayutanachot.jirawat.core.bank.controller;

import com.pattanayutanachot.jirawat.core.bank.dto.*;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/register-teller")
    @PreAuthorize("hasRole('SUPER_ADMIN')") // Only Super Admin can register Tellers
    public ResponseEntity<String> registerTeller(@AuthenticationPrincipal UserDetails userDetails,
                                                 @Valid @RequestBody RegisterRequest request) {
        User adminUser = (User) userDetails; // Cast to actual User entity
        return ResponseEntity.ok(authService.registerTeller(request, adminUser.getId()));
    }

    @PostMapping("/complete-profile")
    @PreAuthorize("isAuthenticated()") // Ensure the user is logged in
    public ResponseEntity<String> completeProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                  @Valid @RequestBody CompleteProfileRequest request) {
        User user = (User) userDetails;  // Cast to get actual user entity
        return ResponseEntity.ok(authService.completeProfile(user.getId(), request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        authService.logout(user.getId());
        return ResponseEntity.ok("User logged out successfully.");
    }
}