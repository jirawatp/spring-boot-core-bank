package com.pattanayutanachot.jirawat.core.bank.controller;

import com.pattanayutanachot.jirawat.core.bank.dto.CreateAccountRequest;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teller/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // Require JWT token
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<String> createAccount(@AuthenticationPrincipal UserDetails tellerDetails,
                                                @RequestBody CreateAccountRequest request) {
        Long tellerId = ((User) tellerDetails).getId(); // Get authenticated teller ID
        return ResponseEntity.ok(accountService.createAccount(request, tellerId));
    }
}