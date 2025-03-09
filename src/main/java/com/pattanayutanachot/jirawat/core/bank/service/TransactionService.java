package com.pattanayutanachot.jirawat.core.bank.service;

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
    public List<TransactionResponse> getBankStatement(Long customerId, int year, int month, String pin) {
        // Find customer accounts
        List<Account> accounts = accountRepository.findByUserId(customerId);

        if (accounts.isEmpty()) {
            throw new RuntimeException("No accounts found for this customer.");
        }

        // Verify PIN
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found."));
        if (!customer.getPin().equals(pin)) {
            throw new RuntimeException("Invalid PIN.");
        }

        // Fetch transactions for the given month
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        List<Transaction> transactions = transactionRepository
                .findByAccountInAndCreatedAtBetweenOrderByCreatedAtDesc(accounts, startOfMonth, endOfMonth);

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
}