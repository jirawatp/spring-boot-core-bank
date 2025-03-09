package com.pattanayutanachot.jirawat.core.bank.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilTest {

    private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @BeforeEach
    void setUp() throws Exception {
        keyPair = CryptoUtil.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    @Test
    void testGenerateKeyPair_ShouldReturnNonNullKeys() {
        assertNotNull(publicKey, "Public key should not be null");
        assertNotNull(privateKey, "Private key should not be null");
    }

    @Test
    void testEncryptAndDecrypt_ShouldReturnOriginalData() throws Exception {
        String originalData = "Hello, Secure World!";

        // Encrypt data
        String encryptedData = CryptoUtil.encrypt(originalData, publicKey);
        assertNotNull(encryptedData, "Encrypted data should not be null");

        // Decrypt data
        String decryptedData = CryptoUtil.decrypt(encryptedData, privateKey);
        assertEquals(originalData, decryptedData, "Decrypted data should match the original data");
    }

    @Test
    void testDecrypt_ShouldThrowException_WhenUsingWrongPrivateKey() throws Exception {
        KeyPair wrongKeyPair = CryptoUtil.generateKeyPair();
        PrivateKey wrongPrivateKey = wrongKeyPair.getPrivate();

        String originalData = "Sensitive Information";
        String encryptedData = CryptoUtil.encrypt(originalData, publicKey);

        Exception exception = assertThrows(Exception.class, () ->
                CryptoUtil.decrypt(encryptedData, wrongPrivateKey)
        );

        assertNotNull(exception.getMessage(), "Exception message should not be null");
    }
}