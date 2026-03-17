package com.payflow.accountservice.service;

import com.payflow.accountservice.dto.AccountResponse;
import com.payflow.accountservice.dto.CreateAccountRequest;

import java.util.List;

public interface AccountService {

    AccountResponse createAccount (CreateAccountRequest request);

    AccountResponse findById(Long id);

    List<AccountResponse> findByUserId(Long userId);

    void deleteById(Long id);
}
