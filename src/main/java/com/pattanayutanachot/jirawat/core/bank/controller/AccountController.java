package com.pattanayutanachot.jirawat.core.bank.controller;

import com.pattanayutanachot.jirawat.core.bank.dto.AccountResponse;
import com.pattanayutanachot.jirawat.core.bank.dto.CreateAccountRequest;
import com.pattanayutanachot.jirawat.core.bank.dto.DepositRequest;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import com.pattanayutanachot.jirawat.core.bank.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // Requires authentication
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    /**
     * Create a new account (Only for TELLERS).
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_TELLER')") // Ensure only TELLER can create accounts
    public ResponseEntity<String> createAccount(@AuthenticationPrincipal UserDetails tellerDetails,
                                                @Valid @RequestBody CreateAccountRequest request) {
        User teller = userRepository.findByEmail(tellerDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Teller not found."));

        return ResponseEntity.ok(accountService.createAccount(request, teller.getId()));
    }

    /**
     * Deposit money into an account (Only for TELLERS).
     */
    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority('ROLE_TELLER')") // Ensure only TELLER can deposit money
    public ResponseEntity<String> deposit(@AuthenticationPrincipal UserDetails tellerDetails,
                                          @Valid @RequestBody DepositRequest request) {
        User teller = userRepository.findByEmail(tellerDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Teller not found."));

        return ResponseEntity.ok(accountService.deposit(request, teller.getId()));
    }

    /**
     * Retrieve account information (Only for CUSTOMERS).
     */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<AccountResponse>> getCustomerAccount(@AuthenticationPrincipal UserDetails customerDetails) {
        User customer = userRepository.findByEmail(customerDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Customer not found."));
        return ResponseEntity.ok(accountService.getCustomerAccounts(customer.getId()));
    }
}