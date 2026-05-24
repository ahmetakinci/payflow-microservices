package com.payflow.paymentservice.service;

import com.payflow.paymentservice.dto.PaymentResponse;
import com.payflow.paymentservice.dto.TransferRequest;

import java.util.List;

public interface PaymentService {

    PaymentResponse transfer(Long userId, TransferRequest request);

    List<PaymentResponse> findAllForUser(Long userId);

    PaymentResponse findByIdForUser(Long id, Long userId);
}
