package com.pattanayutanachot.jirawat.core.bank.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerifyTransferResponseTest {

    @Test
    void verifyTransferResponse_ShouldBeValid() {
        VerifyTransferResponse response = VerifyTransferResponse.builder()
                .canTransfer(true)
                .message("Transfer can proceed.")
                .recipientThaiName("สมชาย ใจดี")
                .recipientEnglishName("Somchai Jaidee")
                .build();

        assertNotNull(response);
        assertTrue(response.canTransfer());
        assertEquals("Transfer can proceed.", response.message());
        assertEquals("สมชาย ใจดี", response.recipientThaiName());
        assertEquals("Somchai Jaidee", response.recipientEnglishName());
    }

    @Test
    void verifyTransferResponse_ShouldHandleFailureScenario() {
        VerifyTransferResponse response = VerifyTransferResponse.builder()
                .canTransfer(false)
                .message("Insufficient balance.")
                .recipientThaiName("สมหญิง ใจงาม")
                .recipientEnglishName("Somying Jaingam")
                .build();

        assertNotNull(response);
        assertFalse(response.canTransfer());
        assertEquals("Insufficient balance.", response.message());
        assertEquals("สมหญิง ใจงาม", response.recipientThaiName());
        assertEquals("Somying Jaingam", response.recipientEnglishName());
    }

    @Test
    void verifyTransferResponse_ShouldHandleNullRecipientNames() {
        VerifyTransferResponse response = VerifyTransferResponse.builder()
                .canTransfer(false)
                .message("Recipient account not found.")
                .recipientThaiName(null)
                .recipientEnglishName(null)
                .build();

        assertNotNull(response);
        assertFalse(response.canTransfer());
        assertEquals("Recipient account not found.", response.message());
        assertNull(response.recipientThaiName());
        assertNull(response.recipientEnglishName());
    }
}