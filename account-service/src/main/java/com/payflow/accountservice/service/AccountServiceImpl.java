package com.payflow.accountservice.service;

import com.payflow.accountservice.client.UserServiceClient;
import com.payflow.accountservice.dto.AccountResponse;
import com.payflow.accountservice.dto.CreateAccountRequest;
import com.payflow.accountservice.entity.Account;
import com.payflow.accountservice.exception.AccountNotFoundException;
import com.payflow.accountservice.exception.AccountNumberGenerationException;
import com.payflow.accountservice.exception.InsufficientBalanceException;
import com.payflow.accountservice.exception.UserNotFoundException;
import com.payflow.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserServiceClient userServiceClient;

    private static final int MAX_ATTEMPTS = 10;
    private static final Random RANDOM = new Random();

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        if (!userServiceClient.userExists(request.getUserId())) {
            throw new UserNotFoundException(request.getUserId());
        }

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .userId(request.getUserId())
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .build();

        Account saved = accountRepository.save(account);
        return toResponse(saved);
    }

    @Override
    public AccountResponse findById(Long id) {
        Account findById = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
        return toResponse(findById);
    }

    @Override
    public List<AccountResponse> findByUserId(Long userId) {
        return accountRepository.findAccountsByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    private String generateAccountNumber() {

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(RANDOM.nextInt(10)); // 0-9 arası rakam
            }
            String accountNumber = sb.toString();

            if (!accountRepository.existsByAccountNumber(accountNumber)) {
                return accountNumber;
            }
        }
        throw new AccountNumberGenerationException();
    }

    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .accountType(account.getAccountType())
                .createdDate(account.getCreatedDate())
                .build();
    }

    @Override
    public AccountResponse findByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return toResponse(account);
    }

    @Override
    public void debit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        if (amount.compareTo(account.getBalance()) > 0){
            throw new InsufficientBalanceException(accountNumber);
        }

        account.setBalance(account.getBalance().subtract(amount));

        accountRepository.save(account);
    }

    @Override
    public void credit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        account.setBalance(account.getBalance().add(amount));

        accountRepository.save(account);
    }
}