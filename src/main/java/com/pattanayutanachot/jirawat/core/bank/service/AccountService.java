package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.CreateAccountRequest;
import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.model.RoleType;
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
     */
    @Transactional
    public String createAccount(CreateAccountRequest request, Long tellerId) {
        log.info("Teller [{}] is creating an account for Citizen ID: {}", tellerId, request.citizenId());

        User user = userRepository.findByCitizenId(request.citizenId())
                .filter(u -> u.getRoles().stream().anyMatch(role -> role.getName() == RoleType.CUSTOMER))
                .orElseThrow(() -> new RuntimeException("No CUSTOMER user found with this Citizen ID."));

        User teller = userRepository.findById(tellerId)
                .orElseThrow(() -> new UsernameNotFoundException("Teller not found."));

        String accountNumber = generateUniqueAccountNumber();

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
     */
    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%07d", RANDOM.nextInt(10_000_000));
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}