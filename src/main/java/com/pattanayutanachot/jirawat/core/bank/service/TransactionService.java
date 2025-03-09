package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.DepositRequest;
import com.pattanayutanachot.jirawat.core.bank.dto.TransactionResponse;
import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.model.Transaction;
import com.pattanayutanachot.jirawat.core.bank.model.TransactionType;
import com.pattanayutanachot.jirawat.core.bank.repository.AccountRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.TransactionRepository;
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
}