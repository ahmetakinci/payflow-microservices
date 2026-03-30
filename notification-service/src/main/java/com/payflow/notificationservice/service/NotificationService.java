package com.payflow.notificationservice.service;

import com.payflow.notificationservice.dto.PaymentEvent;

public interface NotificationService {
    void sendPaymentNotification(PaymentEvent event);
}
