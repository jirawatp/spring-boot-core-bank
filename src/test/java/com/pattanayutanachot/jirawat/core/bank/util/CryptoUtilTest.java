package com.pattanayutanachot.jirawat.core.bank.util;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilTest {

    @Test
    void testEncryptionAndDecryption() throws Exception {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        String originalText = "HelloWorld";

        String encryptedText = CryptoUtil.encrypt(originalText, keyPair.getPublic());
        String decryptedText = CryptoUtil.decrypt(encryptedText, keyPair.getPrivate());

        assertEquals(originalText, decryptedText);
    }

    @Test
    void testDecryptionWithWrongKey_ShouldFail() throws Exception {
        KeyPair keyPair1 = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        KeyPair keyPair2 = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        String originalText = "SensitiveData";

        String encryptedText = CryptoUtil.encrypt(originalText, keyPair1.getPublic());

        Exception exception = assertThrows(Exception.class, () ->
                CryptoUtil.decrypt(encryptedText, keyPair2.getPrivate())
        );

        assertNotNull(exception.getMessage());
    }
}