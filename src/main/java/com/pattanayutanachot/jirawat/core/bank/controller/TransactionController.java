package com.pattanayutanachot.jirawat.core.bank.controller;

import com.pattanayutanachot.jirawat.core.bank.dto.BankStatementResponse;
import com.pattanayutanachot.jirawat.core.bank.dto.DepositRequest;
import com.pattanayutanachot.jirawat.core.bank.dto.TransactionResponse;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import com.pattanayutanachot.jirawat.core.bank.service.TransactionService;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // Requires authentication
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    /**
     * Retrieve all transactions (Only for TELLERS).
     */
    @GetMapping("/transactions")
    @PreAuthorize("hasAuthority('ROLE_TELLER')")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    /**
     * Retrieve bank statement for a specific month (Only for CUSTOMERS).
     */
    @GetMapping("/statement")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')") // Only customers can access
    public ResponseEntity<List<BankStatementResponse>> getBankStatement(
            @AuthenticationPrincipal UserDetails customerDetails,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam String pin,
            @RequestParam String accountNumber) {

        User customer = userRepository.findByEmail(customerDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Customer not found."));

        return ResponseEntity.ok(transactionService.getBankStatement(customer.getId(), year, month, pin, accountNumber));
    }
}