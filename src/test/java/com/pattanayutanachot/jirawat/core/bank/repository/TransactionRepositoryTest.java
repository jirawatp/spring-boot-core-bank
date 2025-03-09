package com.pattanayutanachot.jirawat.core.bank.repository;

import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.model.Transaction;
import com.pattanayutanachot.jirawat.core.bank.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void testFindByAccountId() {
        Account account = accountRepository.save(Account.builder()
                .accountNumber("1234567")
                .balance(BigDecimal.valueOf(1000))
                .build());

        Transaction transaction = transactionRepository.save(Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(500))
                .createdAt(LocalDateTime.now())
                .build());

        List<Transaction> transactions = transactionRepository.findByAccountId(account.getId());

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        assertEquals(transaction.getAmount(), transactions.get(0).getAmount());
    }

    @Test
    void testFindByAccountAndCreatedAtBetweenOrderByCreatedAtAsc() {
        Account account = accountRepository.save(Account.builder()
                .accountNumber("7654321")
                .balance(BigDecimal.valueOf(2000))
                .build());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);

        Transaction transaction1 = transactionRepository.save(Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(500))
                .createdAt(now.minusHours(5))
                .build());

        Transaction transaction2 = transactionRepository.save(Transaction.builder()
                .account(account)
                .type(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(200))
                .createdAt(now.minusHours(2))
                .build());

        List<Transaction> transactions = transactionRepository.findByAccountAndCreatedAtBetweenOrderByCreatedAtAsc(account, start, end);

        assertEquals(2, transactions.size());
        assertEquals(transaction1.getAmount(), transactions.get(0).getAmount());
        assertEquals(transaction2.getAmount(), transactions.get(1).getAmount());
    }
}