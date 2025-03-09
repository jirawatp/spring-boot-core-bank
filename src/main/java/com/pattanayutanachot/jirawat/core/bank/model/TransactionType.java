package com.pattanayutanachot.jirawat.core.bank.model;

public enum TransactionType {
    DEPOSIT("A0"),
    WITHDRAWAL("A1"),
    TRANSFER("A3");

    private final String code;

    TransactionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}