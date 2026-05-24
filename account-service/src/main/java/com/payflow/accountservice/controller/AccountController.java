package com.payflow.accountservice.controller;

import com.payflow.accountservice.dto.AccountResponse;
import com.payflow.accountservice.dto.CreateAccountRequest;
import com.payflow.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> registerAccount(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(accountService.findByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(accountService.findByIdForUser(id, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        accountService.deleteByIdForUser(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.findByAccountNumberForUser(accountNumber, userId));
    }
}
