package com.pattanayutanachot.jirawat.core.bank.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionChannelTest {

    @Test
    void testTransactionChannelValues() {
        TransactionChannel[] channels = TransactionChannel.values();
        assertNotNull(channels);
        assertEquals(4, channels.length);
        assertEquals(TransactionChannel.ATM, TransactionChannel.valueOf("ATM"));
        assertEquals(TransactionChannel.OTC, TransactionChannel.valueOf("OTC"));
        assertEquals(TransactionChannel.ONLINE, TransactionChannel.valueOf("ONLINE"));
        assertEquals(TransactionChannel.ATS, TransactionChannel.valueOf("ATS"));
    }
}