package com.payflow.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountResponse {
    private Long userId;
    private String accountNumber;
    private BigDecimal balance;
}
