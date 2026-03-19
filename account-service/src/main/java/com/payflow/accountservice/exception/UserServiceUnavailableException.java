package com.payflow.accountservice.exception;

public class UserServiceUnavailableException extends RuntimeException {
    public UserServiceUnavailableException() {
        super("User service is currently unavailable");
    }
}
