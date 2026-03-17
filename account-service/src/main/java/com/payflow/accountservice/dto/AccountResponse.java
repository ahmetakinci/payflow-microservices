package com.payflow.accountservice.dto;

import com.payflow.accountservice.enums.AccountType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class AccountResponse {

    private Long id;
    private Long userId;
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    private LocalDateTime createdDate;
}
