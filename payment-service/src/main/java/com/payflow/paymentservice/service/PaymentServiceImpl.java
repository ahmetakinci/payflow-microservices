package com.payflow.paymentservice.service;

import com.payflow.paymentservice.client.AccountServiceClient;
import com.payflow.paymentservice.dto.AccountResponse;
import com.payflow.paymentservice.dto.PaymentEvent;
import com.payflow.paymentservice.dto.PaymentResponse;
import com.payflow.paymentservice.dto.TransferRequest;
import com.payflow.paymentservice.entity.Payment;
import com.payflow.paymentservice.enums.PaymentStatus;
import com.payflow.paymentservice.exception.AccountNotFoundException;
import com.payflow.paymentservice.exception.InsufficientBalanceException;
import com.payflow.paymentservice.exception.PaymentFailedException;
import com.payflow.paymentservice.exception.PaymentNotFoundException;
import com.payflow.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountServiceClient accountServiceClient;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Override
    public PaymentResponse transfer(Long userId, TransferRequest request) {
        AccountResponse sender = accountServiceClient.getAccount(request.getSenderAccountNumber());
        if (!userId.equals(sender.getUserId())) {
            throw new AccountNotFoundException(request.getSenderAccountNumber());
        }
        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
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

        PaymentEvent event = PaymentEvent.builder()
                .paymentId(saved.getId())
                .senderAccountNumber(saved.getSenderAccountNumber())
                .receiverAccountNumber(saved.getReceiverAccountNumber())
                .amount(saved.getAmount())
                .status(saved.getStatus().name())
                .description(saved.getDescription())
                .build();

        kafkaTemplate.send("payment-events", event);
        return toResponse(saved);
    }

    @Override
    public List<PaymentResponse> findAllForUser(Long userId) {
        Set<String> userAccounts = userAccountNumbers(userId);
        if (userAccounts.isEmpty()) {
            return List.of();
        }
        return paymentRepository
                .findBySenderAccountNumberInOrReceiverAccountNumberIn(userAccounts, userAccounts)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse findByIdForUser(Long id, Long userId) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        Set<String> userAccounts = userAccountNumbers(userId);
        if (!userAccounts.contains(payment.getSenderAccountNumber())
                && !userAccounts.contains(payment.getReceiverAccountNumber())) {
            throw new PaymentNotFoundException(id);
        }
        return toResponse(payment);
    }

    private Set<String> userAccountNumbers(Long userId) {
        return accountServiceClient.getAccountsByUser(userId)
                .stream()
                .map(AccountResponse::getAccountNumber)
                .collect(Collectors.toSet());
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
