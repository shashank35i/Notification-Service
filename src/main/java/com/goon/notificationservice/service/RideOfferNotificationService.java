package com.goon.notificationservice.service;

import com.goon.notificationservice.dto.RideOfferedPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RideOfferNotificationService {

    private final FCMPushService fcmPushService;
    private final StringRedisTemplate redisTemplate;

    public void handleRideOffered(RideOfferedPayload event) {

        if (!event.expiresAt().isAfter(Instant.now())) {
            return;
        }

        String channel = "driver:" + event.driverId() + ":offers";
        String payload = JsonMapper.shared().writeValueAsString(event);

        redisTemplate.execute((RedisCallback<Object>) connection -> {
            byte[] channelBytes = channel.getBytes(StandardCharsets.UTF_8);
            byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);

            return connection.execute("SPUBLISH", channelBytes, payloadBytes);
        });

        if (!event.expiresAt().isAfter(Instant.now())) {
            return;
        }

        Map<String, String> data = Map.of(
                "type", "RIDE_OFFERED",
                "offerId", event.offerId().toString(),
                "rideId", event.rideId().toString(),
                "pickupLat", String.valueOf(event.pickupLat()),
                "pickupLon", String.valueOf(event.pickupLon()),
                "dropLat", String.valueOf(event.dropLat()),
                "dropLon", String.valueOf(event.dropLon()),
                "estimatedFare", event.estimatedFare().toPlainString(),
                "currency", event.currency(),
                "expiresAt", event.expiresAt().toString()
        );

        try {
            fcmPushService.sendNotification(
                    event.driverId(),
                    "New Ride Offer",
                    "You have a new ride request",
                    data
            );
        } catch (Exception e) {
            throw new IllegalStateException("FCM notification failed", e);
        }
    }
}