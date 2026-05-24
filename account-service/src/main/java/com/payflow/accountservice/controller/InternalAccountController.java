package com.payflow.accountservice.controller;

import com.payflow.accountservice.dto.AccountResponse;
import com.payflow.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/accounts")
public class InternalAccountController {

    private final AccountService accountService;

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.findByUserId(userId));
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
