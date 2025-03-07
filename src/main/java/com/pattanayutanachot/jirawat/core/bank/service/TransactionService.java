package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.exception.AccountNotFoundException;
import com.pattanayutanachot.jirawat.core.bank.exception.TransactionFailedException;
import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.model.Transaction;
import com.pattanayutanachot.jirawat.core.bank.repository.AccountRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<Transaction> getAllTransactions() {
        logger.info("Fetching all transactions");
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        logger.info("Fetching transaction with ID: {}", id);
        return transactionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Transaction not found with ID: {}", id);
                    return new TransactionFailedException("Transaction not found with id: " + id);
                });
    }

    @Transactional
    public Transaction deposit(Transaction transaction) {
        logger.info("Processing deposit for account ID: {} with amount: {}", transaction.getAccount().getId(), transaction.getAmount());
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(transaction.getAmount()));
        accountRepository.save(account);

        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Deposit successful for account ID: {} with amount: {}", transaction.getAccount().getId(), transaction.getAmount());
        return savedTransaction;
    }

    @Transactional
    public Transaction withdraw(Transaction transaction) {
        logger.info("Processing withdrawal for account ID: {} with amount: {}", transaction.getAccount().getId(), transaction.getAmount());
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
            logger.warn("Insufficient balance for withdrawal on account ID: {}", transaction.getAccount().getId());
            throw new TransactionFailedException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        accountRepository.save(account);

        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Withdrawal successful for account ID: {} with amount: {}", transaction.getAccount().getId(), transaction.getAmount());
        return savedTransaction;
    }

    @Transactional
    public Transaction transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        logger.info("Processing transfer from account ID: {} to account ID: {} with amount: {}", fromAccountId, toAccountId, amount);
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Recipient account not found"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            logger.warn("Insufficient balance for transfer from account ID: {}", fromAccountId);
            throw new TransactionFailedException("Insufficient balance");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setAccount(fromAccount);
        transaction.setType("TRANSFER");
        transaction.setAmount(amount);

        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Transfer successful from account ID: {} to account ID: {} with amount: {}", fromAccountId, toAccountId, amount);
        return savedTransaction;
    }
}