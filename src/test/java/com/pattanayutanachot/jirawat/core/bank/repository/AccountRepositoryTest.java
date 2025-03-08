package com.pattanayutanachot.jirawat.core.bank.repository;

import com.pattanayutanachot.jirawat.core.bank.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void testFindByAccountNumber() {
        accountRepository.deleteAll();

        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setThaiName("จิรวัฒน์");
        account.setEnglishName("Jirawat");
        account.setPin("1234");
        account.setCitizenId("1234567890123");
        account.setPassword("securepassword");
        account.setBalance(BigDecimal.valueOf(1000.00));
        account.setEmail("test@example.com");  // ✅ Ensure email is set

        accountRepository.save(account);

        boolean exists = accountRepository.findByAccountNumber(account.getAccountNumber()).isPresent();
        assertTrue(exists);
    }
}