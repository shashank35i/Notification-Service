package com.goon.notificationservice.worker;


import com.google.firebase.messaging.FirebaseMessagingException;
import com.goon.notificationservice.dto.DriverAssignedPayload;
import com.goon.notificationservice.dto.RideOfferedPayload;
import com.goon.notificationservice.entity.ProcessedEvent;
import com.goon.notificationservice.repository.ProcessedEventRepository;
import com.goon.notificationservice.service.*;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationPushWorker {

    private final ProcessedEventRepository processedRepository;
    private final RideOfferNotificationService offerService;
    private final RideNotificationService rideService;

    @KafkaListener(
            topics = "ride-offered",
            groupId = "notification-service"
    )
    public void consumeRideOffered(
            String message,
            @Header("eventId") String eventId,
            Acknowledgment ack) {

        UUID id = UUID.fromString(eventId);

        if (processedRepository.existsById(id)) {
            ack.acknowledge();
            return;
        }

        RideOfferedPayload event = JsonMapper.shared()
                .readValue(message, RideOfferedPayload.class);

        if (event.expiresAt().isAfter(Instant.now())) {
            offerService.handleRideOffered(event);
        }

        processedRepository.saveAndFlush(
                new ProcessedEvent(id, Instant.now())
        );

        ack.acknowledge();
    }

    @KafkaListener(
            topics = "driver-assigned",
            groupId = "notification-service"
    )
    public void consumeDriverAssigned(
            String message,
            @Header("eventId") String eventId,
            Acknowledgment ack) throws FirebaseMessagingException {

        UUID id = UUID.fromString(eventId);

        if (processedRepository.existsById(id)) {
            ack.acknowledge();
            return;
        }

        DriverAssignedPayload event = JsonMapper.shared()
                .readValue(message, DriverAssignedPayload.class);

        rideService.handleDriverAssigned(event);

        processedRepository.saveAndFlush(
                new ProcessedEvent(id, Instant.now())
        );

        ack.acknowledge();
    }
}