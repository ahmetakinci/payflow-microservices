package com.payflow.paymentservice.exception;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String senderAccountNumber) {
        super("Payment Failed: " + senderAccountNumber);
    }
}