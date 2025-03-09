package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.AccountResponse;
import com.pattanayutanachot.jirawat.core.bank.dto.CreateAccountRequest;
import com.pattanayutanachot.jirawat.core.bank.dto.DepositRequest;
import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.model.Transaction;
import com.pattanayutanachot.jirawat.core.bank.model.TransactionType;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.repository.AccountRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.TransactionRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Creates a new bank account for a CUSTOMER.
     * Only a TELLER can perform this action.
     */
    @Transactional
    public String createAccount(CreateAccountRequest request, Long tellerId) {
        log.info("Teller [{}] is creating an account for Citizen ID: {}", tellerId, request.citizenId());

        // Find existing CUSTOMER user by citizenId
        User user = userRepository.findByCitizenId(request.citizenId())
                .orElseThrow(() -> new UsernameNotFoundException("Customer with Citizen ID not found."));

        if (!Objects.equals(user.getThaiName(), request.thaiName()) || !Objects.equals(user.getEnglishName(), request.englishName()) ) {
            throw new RuntimeException("CUSTOMER info invalids please re-kyc.");
        }

        // Ensure that only CUSTOMER roles can have a new account
        boolean isCustomer = user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("CUSTOMER"));

        if (!isCustomer) {
            throw new RuntimeException("Only CUSTOMER users can have bank accounts.");
        }

        // Find the TELLER
        User teller = userRepository.findById(tellerId)
                .orElseThrow(() -> new UsernameNotFoundException("Teller not found."));

        // Generate a unique 7-digit account number
        String accountNumber = generateUniqueAccountNumber();

        // Ensure the initial deposit is non-negative
        BigDecimal initialDeposit = request.initialDeposit() != null ? request.initialDeposit() : BigDecimal.ZERO;

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(initialDeposit)
                .user(user)
                .createdByTeller(teller)
                .build();

        accountRepository.save(account);

        // Log the transaction if an initial deposit is provided
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            Transaction depositTransaction = Transaction.builder()
                    .account(account)
                    .type(TransactionType.DEPOSIT)
                    .amount(initialDeposit)
                    .build();
            transactionRepository.save(depositTransaction);
        }

        log.info("Account [{}] created successfully by Teller [{}]", accountNumber, tellerId);
        return "Account created successfully with number: " + accountNumber;
    }

    /**
     * Deposits money into an existing account.
     * Only a TELLER can perform this action.
     */
    @Transactional
    public String deposit(DepositRequest request, Long tellerId) {
        log.info("Teller [{}] is depositing {} THB into account [{}]", tellerId, request.amount(), request.accountNumber());

        // Validate amount
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ONE) < 0) {
            throw new RuntimeException("Deposit amount must be at least 1 THB.");
        }

        // Find the account by account number
        Account account = accountRepository.findByAccountNumber(request.accountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found."));

        // Update account balance
        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);

        // Log the transaction
        Transaction depositTransaction = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(request.amount())
                .build();
        transactionRepository.save(depositTransaction);

        log.info("Successfully deposited {} THB into account [{}] by Teller [{}]", request.amount(), request.accountNumber(), tellerId);
        return "Deposit successful. New balance: " + account.getBalance();
    }

    /**
     * Retrieves account details for the authenticated customer.
     */
    public List<AccountResponse> getCustomerAccounts(Long customerId) {
        List<Account> accounts = accountRepository.findByUserId(customerId);

        return accounts.stream()
                .map(account -> new AccountResponse(
                        account.getAccountNumber(),
                        account.getBalance(),
                        account.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Generates a unique 7-digit account number.
     */
    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%07d", RANDOM.nextInt(10_000_000)); // Generate 7-digit number
        } while (accountRepository.existsByAccountNumber(accountNumber)); // Ensure uniqueness

        return accountNumber;
    }
}