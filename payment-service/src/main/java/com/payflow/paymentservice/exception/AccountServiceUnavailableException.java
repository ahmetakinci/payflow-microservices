package com.payflow.paymentservice.exception;

public class AccountServiceUnavailableException extends RuntimeException {
    public AccountServiceUnavailableException() {
        super("Account service is currently unavailable");
    }
}
