package com.pattanayutanachot.jirawat.core.bank.repository;

import com.pattanayutanachot.jirawat.core.bank.model.Account;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindByAccountNumber() {
        User user = userRepository.save(User.builder()
                .email("test@example.com")
                .password("securepassword")
                .build());

        Account account = accountRepository.save(Account.builder()
                .accountNumber("1234567")
                .balance(BigDecimal.valueOf(1000))
                .user(user)
                .build());

        Optional<Account> foundAccount = accountRepository.findByAccountNumber("1234567");
        assertTrue(foundAccount.isPresent());
        assertEquals("1234567", foundAccount.get().getAccountNumber());
    }

    @Test
    void testExistsByAccountNumber() {
        Account account = accountRepository.save(Account.builder()
                .accountNumber("7654321")
                .balance(BigDecimal.valueOf(2000))
                .build());

        assertTrue(accountRepository.existsByAccountNumber("7654321"));
        assertFalse(accountRepository.existsByAccountNumber("0000000"));
    }

    @Test
    void testFindByUserId() {
        User user = userRepository.save(User.builder()
                .email("customer@example.com")
                .password("securepassword")
                .build());

        accountRepository.save(Account.builder()
                .accountNumber("1122334")
                .balance(BigDecimal.valueOf(1500))
                .user(user)
                .build());

        List<Account> accounts = accountRepository.findByUserId(user.getId());
        assertFalse(accounts.isEmpty());
        assertEquals(1, accounts.size());
        assertEquals("1122334", accounts.get(0).getAccountNumber());
    }

    @Test
    void testFindByUser() {
        User user = userRepository.save(User.builder()
                .email("user@example.com")
                .password("securepassword")
                .build());

        Account account = accountRepository.save(Account.builder()
                .accountNumber("9988776")
                .balance(BigDecimal.valueOf(500))
                .user(user)
                .build());

        Optional<Account> foundAccount = accountRepository.findByUser(user);
        assertTrue(foundAccount.isPresent());
        assertEquals("9988776", foundAccount.get().getAccountNumber());
    }
}