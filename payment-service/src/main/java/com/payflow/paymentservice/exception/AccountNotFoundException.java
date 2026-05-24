package com.payflow.paymentservice.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }
}
