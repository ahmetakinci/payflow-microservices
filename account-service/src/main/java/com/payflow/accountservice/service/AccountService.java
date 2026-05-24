package com.payflow.accountservice.service;

import com.payflow.accountservice.dto.AccountResponse;
import com.payflow.accountservice.dto.CreateAccountRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    AccountResponse createAccount(Long userId, CreateAccountRequest request);

    List<AccountResponse> findByUserId(Long userId);

    AccountResponse findByIdForUser(Long id, Long userId);

    void deleteByIdForUser(Long id, Long userId);

    AccountResponse findByAccountNumberForUser(String accountNumber, Long userId);

    AccountResponse findByAccountNumber(String accountNumber);

    void debit(String accountNumber, BigDecimal amount);

    void credit(String accountNumber, BigDecimal amount);
}
