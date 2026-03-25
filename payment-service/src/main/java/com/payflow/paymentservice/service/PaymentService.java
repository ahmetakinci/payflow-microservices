package com.payflow.paymentservice.service;

import com.payflow.paymentservice.dto.PaymentResponse;
import com.payflow.paymentservice.dto.TransferRequest;

import java.util.List;

public interface PaymentService {
    PaymentResponse transfer(TransferRequest request);

    List<PaymentResponse> findAll();

    PaymentResponse findById(Long id);
}
