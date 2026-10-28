package com.goon.notificationservice.worker;
import com.goon.notificationservice.dto.PaymentResultPayload;
import com.goon.notificationservice.service.PaymentNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.*;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import java.util.UUID;

@Component @RequiredArgsConstructor
public class PaymentNotificationWorker {
    private final PaymentNotificationService notifications;
    @KafkaListener(topics = {"payment-completed", "payment-failed"}, groupId = "notification-payment-status",
            containerFactory = "paymentNotificationKafkaListenerContainerFactory")
    public void consume(String body, @Header("eventId") String eventId,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, Acknowledgment ack) throws Exception {
        notifications.notify(UUID.fromString(eventId), topic,
                JsonMapper.shared().readValue(body, PaymentResultPayload.class));
        ack.acknowledge();
    }
}

