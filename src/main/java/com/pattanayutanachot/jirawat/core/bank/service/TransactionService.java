package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.BankStatementRequest;
import com.pattanayutanachot.jirawat.core.bank.dto.BankStatementResponse;
import com.pattanayutanachot.jirawat.core.bank.dto.DepositRequest;
import com.pattanayutanachot.jirawat.core.bank.dto.TransactionResponse;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves all transactions.
     *
     * @return A list of transaction details.
     */
    public List<TransactionResponse> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(tx -> new TransactionResponse(
                        tx.getId(),
                        tx.getAccount().getAccountNumber(),
                        tx.getType().name(),
                        tx.getAmount(),
                        tx.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a customer's bank statement for a specific month.
     */
    @Transactional(readOnly = true)
    public List<BankStatementResponse> getBankStatement(Long customerId, BankStatementRequest request) {
        log.info("Fetching bank statement for user [{}], accountNumber [{}] - Month: {} Year: {}", customerId, request.accountNumber(), request.month(), request.year());

        Account account = accountRepository.findByAccountNumber(request.accountNumber())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found."));

        if (!account.getUser().getId().equals(customerId)) {
            throw new RuntimeException("You can only access your own account.");
        }

        // Verify PIN
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found."));
        if (!customer.getPin().equals(request.pin())) {
            throw new RuntimeException("Invalid PIN.");
        }

        // Collect all transactions for the given month & year
        List<Transaction> transactions = transactionRepository.findByAccountAndCreatedAtBetweenOrderByCreatedAtAsc(
                account,
                LocalDateTime.of(request.year(), request.month(), 1, 0, 0),
                LocalDateTime.of(request.year(), request.month(), LocalDateTime.now().getDayOfMonth(), 23, 59)
        );

        if (transactions.isEmpty()) {
            return List.of(); // Return empty list if no transactions found
        }

        // Generate bank statement response
        List<BankStatementResponse> bankStatement = new ArrayList<>();
        BigDecimal currentBalance = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            boolean isDeposit = tx.getType() == TransactionType.DEPOSIT;

            bankStatement.add(BankStatementResponse.builder()
                    .accountNumber(tx.getAccount().getAccountNumber())
                    .date(tx.getCreatedAt().toLocalDate().toString()) // Format Date
                    .time(tx.getCreatedAt().toLocalTime().toString()) // Format Time
                    .code(tx.getType().getCode()) // Transaction Code
                    .channel(tx.getChannel().name()) // Channel Info
                    .debit(isDeposit ? null : tx.getAmount()) // Debit if withdrawal
                    .credit(isDeposit ? tx.getAmount() : null) // Credit if deposit
                    .balance(tx.getBalanceAfter()) // Updated Balance
                    .remark(tx.getRemark() != null ? tx.getRemark() : "N/A") // Transaction Description
                    .build());
        }

        return bankStatement;
    }
}