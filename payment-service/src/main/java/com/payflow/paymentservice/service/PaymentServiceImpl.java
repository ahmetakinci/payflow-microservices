package com.payflow.paymentservice.service;

import com.payflow.paymentservice.client.AccountServiceClient;
import com.payflow.paymentservice.dto.PaymentResponse;
import com.payflow.paymentservice.dto.TransferRequest;
import com.payflow.paymentservice.entity.Payment;
import com.payflow.paymentservice.enums.PaymentStatus;
import com.payflow.paymentservice.exception.BalanceNotFoundException;
import com.payflow.paymentservice.exception.InsufficientBalanceException;
import com.payflow.paymentservice.exception.PaymentFailedException;
import com.payflow.paymentservice.exception.PaymentNotFoundException;
import com.payflow.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountServiceClient accountServiceClient;

    @Override
    public PaymentResponse transfer(TransferRequest request) {
        BigDecimal balance = accountServiceClient.getBalance(request.getSenderAccountNumber());
        if (balance == null) {
            throw new BalanceNotFoundException(request.getSenderAccountNumber());
        }
        if (balance.compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(request.getSenderAccountNumber());
        }

        Payment payment = Payment.builder()
                .senderAccountNumber(request.getSenderAccountNumber())
                .receiverAccountNumber(request.getReceiverAccountNumber())
                .amount(request.getAmount())
                .description(request.getDescription())
                .status(PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);

        accountServiceClient.debit(request.getSenderAccountNumber(), request.getAmount());

        try {
            accountServiceClient.credit(request.getReceiverAccountNumber(), request.getAmount());
        } catch (Exception e) {
            accountServiceClient.credit(request.getSenderAccountNumber(), request.getAmount());
            saved.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(saved);
            throw new PaymentFailedException(request.getSenderAccountNumber());
        }

        saved.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(saved);
        return toResponse(saved);
    }

    @Override
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse findById(Long id) {
        Payment findById = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));
        return toResponse(findById);
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .senderAccountNumber(payment.getSenderAccountNumber())
                .receiverAccountNumber(payment.getReceiverAccountNumber())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .description(payment.getDescription())
                .createdDate(payment.getCreatedDate())
                .build();
    }

}
