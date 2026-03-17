package com.payflow.accountservice.dto;

import com.payflow.accountservice.enums.AccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {

    private AccountType accountType;

    private Long userId;
}
