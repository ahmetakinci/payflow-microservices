package com.payflow.accountservice.dto;

import com.payflow.accountservice.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {

    @NotNull
    private AccountType accountType;
}
