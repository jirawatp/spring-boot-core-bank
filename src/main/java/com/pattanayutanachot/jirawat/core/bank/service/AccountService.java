package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.exception.AccountNotFoundException;
import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        logger.info("Fetching all accounts");
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        logger.info("Fetching account with ID: {}", id);
        return accountRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Account not found with ID: {}", id);
                    return new AccountNotFoundException("Account not found with id: " + id);
                });
    }

    public Account createAccount(Account account) {
        logger.info("Creating a new account for: {}", account.getEnglishName());
        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, Account accountDetails) {
        logger.info("Updating account with ID: {}", id);
        Account account = getAccountById(id);
        account.setThaiName(accountDetails.getThaiName());
        account.setEnglishName(accountDetails.getEnglishName());
        account.setEmail(accountDetails.getEmail());
        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        logger.info("Deleting account with ID: {}", id);
        Account account = getAccountById(id);
        accountRepository.delete(account);
    }
}