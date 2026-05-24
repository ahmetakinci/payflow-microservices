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
    public AccountResponse createAccount(Long userId, CreateAccountRequest request) {
        if (!userServiceClient.userExists(userId)) {
            throw new UserNotFoundException(userId);
        }

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .build();

        Account saved = accountRepository.save(account);
        return toResponse(saved);
    }

    @Override
    public List<AccountResponse> findByUserId(Long userId) {
        return accountRepository.findAccountsByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AccountResponse findByIdForUser(Long id, Long userId) {
        return toResponse(loadOwnedById(id, userId));
    }

    @Override
    public void deleteByIdForUser(Long id, Long userId) {
        Account account = loadOwnedById(id, userId);
        accountRepository.delete(account);
    }

    @Override
    public AccountResponse findByAccountNumberForUser(String accountNumber, Long userId) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        if (!account.getUserId().equals(userId)) {
            throw new AccountNotFoundException(accountNumber);
        }
        return toResponse(account);
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

        if (amount.compareTo(account.getBalance()) > 0) {
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

    private Account loadOwnedById(Long id, Long userId) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        if (!account.getUserId().equals(userId)) {
            throw new AccountNotFoundException(id);
        }
        return account;
    }

    private String generateAccountNumber() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(RANDOM.nextInt(10));
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
}
