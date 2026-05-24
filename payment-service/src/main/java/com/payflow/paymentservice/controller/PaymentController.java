package com.payflow.paymentservice.controller;

import com.payflow.paymentservice.dto.PaymentResponse;
import com.payflow.paymentservice.dto.TransferRequest;
import com.payflow.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/transfer")
    public ResponseEntity<PaymentResponse> transfer(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody TransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.transfer(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findAll(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(paymentService.findAllForUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findByIdForUser(id, userId));
    }
}
