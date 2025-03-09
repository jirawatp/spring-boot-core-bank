package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.CreateAccountRequest;
import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.repository.AccountRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Creates a new bank account for a user.
     * Only a TELLER can create an account for a CUSTOMER or PERSON.
     *
     * @param request   the request containing user details
     * @param tellerId  the ID of the teller creating the account
     * @return a success message with the generated account number
     */
    @Transactional
    public String createAccount(CreateAccountRequest request, Long tellerId) {
        log.info("Teller [{}] is creating an account for Citizen ID: {}", tellerId, request.citizenId());

        // Find the user by Citizen ID
        User user = userRepository.findByCitizenId(request.citizenId())
                .orElseThrow(() -> new UsernameNotFoundException("User with Citizen ID not found."));

        // Ensure Teller exists
        User teller = userRepository.findById(tellerId)
                .orElseThrow(() -> new UsernameNotFoundException("Teller not found."));

        // Generate a unique 7-digit account number
        String accountNumber = generateUniqueAccountNumber();

        // Ensure initial deposit is non-negative
        BigDecimal initialDeposit = request.initialDeposit() != null ? request.initialDeposit() : BigDecimal.ZERO;

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(initialDeposit)
                .user(user)
                .createdByTeller(teller)
                .build();

        accountRepository.save(account);

        log.info("Account [{}] created successfully by Teller [{}]", accountNumber, tellerId);
        return "Account created successfully with number: " + accountNumber;
    }

    /**
     * Generates a unique 7-digit account number.
     * Ensures uniqueness before returning a valid account number.
     *
     * @return a unique 7-digit account number
     */
    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%07d", RANDOM.nextInt(10_000_000)); // Generate 7-digit number
        } while (accountRepository.existsByAccountNumber(accountNumber)); // Ensure uniqueness

        return accountNumber;
    }
}