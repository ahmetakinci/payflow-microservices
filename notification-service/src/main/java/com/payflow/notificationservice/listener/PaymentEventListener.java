package com.payflow.notificationservice.listener;

import com.payflow.notificationservice.dto.PaymentEvent;
import com.payflow.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "payment-events", groupId = "notification-group")
    public void handlePaymentEvent(PaymentEvent event) {
        log.info("Payment event received: {}", event.getPaymentId());
        notificationService.sendPaymentNotification(event);
    }
}