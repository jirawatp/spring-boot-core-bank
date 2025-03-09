package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.*;
import com.pattanayutanachot.jirawat.core.bank.model.*;
import com.pattanayutanachot.jirawat.core.bank.repository.AccountRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.TransactionRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
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

        // Ensure Teller Exists
        User teller = userRepository.findById(tellerId)
                .orElseThrow(() -> new UsernameNotFoundException("Teller not found."));

        // Ensure the target user exists and is a CUSTOMER
        User customer = userRepository.findByCitizenId(request.citizenId())
                .orElseThrow(() -> new IllegalArgumentException("Customer with Citizen ID not found."));

        if (!Objects.equals(customer.getThaiName(), request.thaiName()) || !Objects.equals(customer.getEnglishName(), request.englishName())) {
            throw new IllegalArgumentException("CUSTOMER info invalid. Please re-verify.");
        }

        // Ensure the user is a CUSTOMER
        boolean isCustomer = customer.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("CUSTOMER"));

        if (!isCustomer) {
            throw new IllegalArgumentException("Only CUSTOMER users can have bank accounts.");
        }

        // Generate Unique 7-Digit Account Number
        String accountNumber = generateUniqueAccountNumber();

        // Ensure the Initial Deposit is Valid
        BigDecimal initialDeposit = request.initialDeposit() != null ? request.initialDeposit() : BigDecimal.ZERO;

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(initialDeposit)
                .user(customer)
                .createdByTeller(teller)
                .build();

        accountRepository.save(account);

        // Log the Transaction If Initial Deposit Exists
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            String remark = "Deposit Terminal " + tellerId + "****";

            Transaction depositTransaction = Transaction.builder()
                    .account(account)
                    .type(TransactionType.DEPOSIT)
                    .amount(initialDeposit)
                    .channel(TransactionChannel.OTC)
                    .remark(remark)
                    .balanceAfter(initialDeposit)
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

        if (request.amount() == null || request.amount().compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("Deposit amount must be at least 1 THB.");
        }

        Account account = accountRepository.findByAccountNumber(request.accountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        BigDecimal newBalance = account.getBalance().add(request.amount());

        account.setBalance(newBalance);
        accountRepository.save(account);

        String remark = "Deposit Terminal " + tellerId + "****";

        Transaction depositTransaction = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(request.amount())
                .channel(TransactionChannel.OTC)
                .remark(remark)
                .balanceAfter(newBalance)
                .build();
        transactionRepository.save(depositTransaction);

        log.info("Successfully deposited {} THB into account [{}] by Teller [{}]", request.amount(), request.accountNumber(), tellerId);
        return "Deposit successful. New balance: " + account.getBalance();
    }

    /**
     * Retrieves account details for the authenticated CUSTOMER.
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> getCustomerAccounts(Long customerId) {
        List<Account> accounts = accountRepository.findByUserId(customerId);
        if (accounts.isEmpty()) {
            log.info("No accounts found for Customer [{}]", customerId);
            return List.of();
        }
        return accounts.stream()
                .map(account -> new AccountResponse(
                        account.getAccountNumber(),
                        account.getBalance(),
                        account.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Transfers money between accounts with PIN validation.
     */
    @Transactional
    public String transferMoney(Long customerId, TransferRequest request) {
        log.info("Customer [{}] is transferring {} THB to account [{}]", customerId, request.amount(), request.toAccountNumber());

        // Find the sender's account
        Account senderAccount = accountRepository.findByAccountNumber(request.fromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Sender account not found."));

        // Verify the sender is the owner of the account
        if (!senderAccount.getUser().getId().equals(customerId)) {
            throw new RuntimeException("You can only transfer money from your own account.");
        }

        // Validate the PIN
        if (!senderAccount.getUser().getPin().equals(request.pin())) {
            throw new RuntimeException("Invalid PIN.");
        }

        // Ensure the recipient account exists
        Account recipientAccount = accountRepository.findByAccountNumber(request.toAccountNumber())
                .orElseThrow(() -> new RuntimeException("Recipient account not found."));

        // Ensure sufficient balance
        if (senderAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException("Insufficient balance.");
        }

        // Deduct from sender
        BigDecimal senderNewBalance = senderAccount.getBalance().subtract(request.amount());
        senderAccount.setBalance(senderNewBalance);
        accountRepository.save(senderAccount);

        // Credit recipient
        BigDecimal recipientNewBalance = recipientAccount.getBalance().add(request.amount());
        recipientAccount.setBalance(recipientNewBalance);
        accountRepository.save(recipientAccount);

        LocalDateTime createdAt = LocalDateTime.now();

        String senderLast4 = senderAccount.getAccountNumber().substring(senderAccount.getAccountNumber().length() - 4);
        String recipientLast4 = recipientAccount.getAccountNumber().substring(recipientAccount.getAccountNumber().length() - 4);

        String receiveRemark = "Receive from x" + senderLast4 + " " + senderAccount.getUser().getEnglishName();
        String transferRemark = "Transfer to x" + recipientLast4 + " " + recipientAccount.getUser().getEnglishName();

        // Log the transaction for both sender and recipient
        Transaction depositTransaction = Transaction.builder()
                .account(recipientAccount)
                .type(TransactionType.DEPOSIT)
                .amount(request.amount())
                .channel(TransactionChannel.ATS)
                .remark(receiveRemark)
                .balanceAfter(recipientNewBalance)
                .createdAt(createdAt)
                .build();
        transactionRepository.save(depositTransaction);

        Transaction withdrawalTransaction = Transaction.builder()
                .account(senderAccount)
                .type(TransactionType.WITHDRAWAL)
                .amount(request.amount())
                .channel(TransactionChannel.ATS)
                .remark(transferRemark)
                .balanceAfter(senderNewBalance)
                .createdAt(createdAt)
                .build();
        transactionRepository.save(withdrawalTransaction);

        log.info("Transfer successful: {} THB from [{}] to [{}]", request.amount(), request.fromAccountNumber(), request.toAccountNumber());
        return "Transfer successful.";
    }

    /**
     * Verifies if a transfer can proceed based on sender balance and recipient existence.
     */
    @Transactional(readOnly = true)
    public VerifyTransferResponse verifyTransfer(Long userId, @Valid VerifyTransferRequest request) {
        log.info("Verifying transfer from [{}] to [{}] with amount [{}]",
                request.withdrawalAccountNumber(), request.recipientAccountNumber(), request.amount());

        // Validate sender account
        Account senderAccount = accountRepository.findByAccountNumber(request.withdrawalAccountNumber())
                .orElseThrow(() -> new RuntimeException("Sender account not found."));

        // Ensure the sender owns the withdrawal account
        if (!senderAccount.getUser().getId().equals(userId)) {
            return VerifyTransferResponse.builder()
                    .canTransfer(false)
                    .message("You can only transfer from your own account.")
                    .build();
        }

        Optional<Account> recipientAccountOpt = accountRepository.findByAccountNumber(request.recipientAccountNumber());
        if (recipientAccountOpt.isEmpty()) {
            return VerifyTransferResponse.builder()
                    .canTransfer(false)
                    .message("Recipient account not found.")
                    .build();
        }

        Account recipientAccount = recipientAccountOpt.get();

        if (senderAccount.getBalance().compareTo(request.amount()) < 0) {
            return VerifyTransferResponse.builder()
                    .canTransfer(false)
                    .message("Insufficient balance.")
                    .recipientThaiName(recipientAccount.getUser().getThaiName())
                    .recipientEnglishName(recipientAccount.getUser().getEnglishName())
                    .build();
        }

        return VerifyTransferResponse.builder()
                .canTransfer(true)
                .message("Transfer can proceed.")
                .recipientThaiName(recipientAccount.getUser().getThaiName())
                .recipientEnglishName(recipientAccount.getUser().getEnglishName())
                .build();
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