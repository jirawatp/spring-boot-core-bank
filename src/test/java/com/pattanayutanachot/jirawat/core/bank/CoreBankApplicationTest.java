package com.pattanayutanachot.jirawat.core.bank;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class CoreBankApplicationTest {

    @Test
    void applicationStartsSuccessfully() {
        assertDoesNotThrow(() -> CoreBankApplication.main(new String[]{}));
    }
}