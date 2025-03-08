package com.pattanayutanachot.jirawat.core.bank.repository;

import com.pattanayutanachot.jirawat.core.bank.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;  // Import Optional

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

//    @Test
//    void testFindByAccountId() {
//        transactionRepository.deleteAll();
//
//        Optional<Transaction> transaction = transactionRepository.findByAccountId(1L).stream().findFirst();
//        assertTrue(transaction.isPresent());
//    }
}