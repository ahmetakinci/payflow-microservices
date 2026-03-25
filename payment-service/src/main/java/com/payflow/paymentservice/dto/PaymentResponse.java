package com.payflow.paymentservice.dto;

import com.payflow.paymentservice.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private PaymentStatus status;
    private String description;
    private LocalDateTime createdDate;
}
