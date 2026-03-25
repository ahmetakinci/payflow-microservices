package com.payflow.paymentservice.exception;

public class BalanceNotFoundException extends RuntimeException {
    public BalanceNotFoundException(String senderAccountNumber) {
        super("Balance not found: " + senderAccountNumber);
    }
}