package com.payflow.accountservice.controller;

import com.payflow.accountservice.dto.AccountResponse;
import com.payflow.accountservice.dto.CreateAccountRequest;
import com.payflow.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> registerAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) {
        AccountResponse response = accountService.createAccount(createAccountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByUser(@PathVariable Long userId) {
        List<AccountResponse> responses = accountService.findByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        AccountResponse response = accountService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.findByAccountNumber(accountNumber));
    }

    @PutMapping("/number/{accountNumber}/debit")
    public ResponseEntity<Void> debit(
            @PathVariable String accountNumber,
            @RequestBody BigDecimal amount) {
        accountService.debit(accountNumber, amount);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/number/{accountNumber}/credit")
    public ResponseEntity<Void> credit(
            @PathVariable String accountNumber,
            @RequestBody BigDecimal amount) {
        accountService.credit(accountNumber, amount);
        return ResponseEntity.noContent().build();
    }
}
