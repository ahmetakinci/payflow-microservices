package com.payflow.notificationservice.service;

import com.payflow.notificationservice.dto.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendPaymentNotification(PaymentEvent event) {
        log.info("Sending notification for payment: {}", event.getPaymentId());
        log.info("Sender: {} → Receiver: {} | Amount: {} | Status: {}",
                event.getSenderAccountNumber(),
                event.getReceiverAccountNumber(),
                event.getAmount(),
                event.getStatus());
    }
}
