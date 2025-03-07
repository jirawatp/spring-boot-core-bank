package com.pattanayutanachot.jirawat.core.bank.exception;

public class TransactionFailedException extends RuntimeException {
    public TransactionFailedException(String message) {
        super(message);
    }
}