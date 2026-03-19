package com.payflow.accountservice.exception;

public class AccountNumberGenerationException extends RuntimeException {
    public AccountNumberGenerationException() {
        super("Could not generate unique account number after maximum attempts");
    }
}
