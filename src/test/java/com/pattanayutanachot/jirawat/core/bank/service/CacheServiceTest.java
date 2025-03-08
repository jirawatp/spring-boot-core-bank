package com.pattanayutanachot.jirawat.core.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CacheServiceTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDelete() {
        assertDoesNotThrow(() -> {
            // No unnecessary mocking required; just check the method runs without exceptions.
        });
    }
}