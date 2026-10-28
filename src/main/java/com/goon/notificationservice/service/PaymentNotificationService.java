package com.goon.notificationservice.service;

import com.goon.notificationservice.dto.PaymentResultPayload;
import com.goon.notificationservice.entity.*;
import com.goon.notificationservice.repository.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor
public class PaymentNotificationService {
    private final ProcessedEventRepository processed;
    private final PaymentNoticeRepository notices;
    private final DeviceTokenRepository tokens;
    private final FCMPushService push;

    @Transactional(rollbackFor = Exception.class)
    public void notify(UUID eventId, String topic, PaymentResultPayload event)
            throws FirebaseMessagingException {
        if (processed.existsById(eventId)) {
            return;
        }
        String expected = "payment-completed".equals(topic) ? "PAID" : "FAILED";
        if (!expected.equals(event.status()) || event.customerId() == null || event.rideId() == null
                || event.paymentId() == null || event.amountMinor() < 0 || event.revision() < 1
                || !"INR".equals(event.currency())) {
            throw new IllegalArgumentException("Invalid payment notification event");
        }
        var state = notices.findForUpdate(event.rideId()).orElse(null);
        if (state == null) {
            state = new PaymentNoticeState();
            state.setRideId(event.rideId());
            state = notices.saveAndFlush(state);
        }
        if (processed.existsById(eventId)) {
            return;
        }
        boolean paid = "PAID".equals(event.status());
        if (!"PAID".equals(state.getStatus()) && (paid || event.revision() > state.getRevision())) {
            if (!tokens.existsById(event.customerId())) {
                throw new IllegalStateException("Customer device token not registered");
            }
            push.sendNotification(event.customerId(), paid ? "Payment received" : "Payment failed",
                    paid ? "Your ride payment is complete" : "Please retry your ride payment",
                    Map.of("type", paid ? "PAYMENT_COMPLETED" : "PAYMENT_FAILED",
                            "rideId", event.rideId().toString(), "paymentId", event.paymentId().toString(),
                            "amount", BigDecimal.valueOf(event.amountMinor(), 2).toPlainString(),
                            "currency", event.currency()));
            state.setRevision(Math.max(state.getRevision(), event.revision()));
            state.setStatus(event.status());
        }
        processed.saveAndFlush(new ProcessedEvent(eventId, Instant.now()));
    }
}

