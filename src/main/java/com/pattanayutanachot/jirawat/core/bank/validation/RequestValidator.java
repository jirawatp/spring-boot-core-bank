package com.pattanayutanachot.jirawat.core.bank.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequestValidator {

    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && accountNumber.matches("^[0-9]{10}$");
    }

    public static boolean isValidPin(String pin) {
        return pin != null && pin.matches("^[0-9]{6}$");
    }
}