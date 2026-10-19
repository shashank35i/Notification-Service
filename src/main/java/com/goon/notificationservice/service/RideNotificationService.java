package com.goon.notificationservice.service;

import com.goon.notificationservice.dto.DriverAssignedPayload;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideNotificationService {

    private final FCMPushService fcmPushService;

    public void handleDriverAssigned(DriverAssignedPayload event)
            throws FirebaseMessagingException {

        Map<String, String> data = Map.of(
                "type", "DRIVER_ASSIGNED",
                "rideId", event.rideId().toString(),
                "driverId", event.driverId().toString(),
                "driverName", event.driverName(),
                "vehicleModel", event.vehicleModel(),
                "vehicleNumber", event.vehicleNumber()
        );

        fcmPushService.sendNotification(
                event.customerId(),
                "Driver Assigned",
                "Your driver is on the way",
                data
        );
    }
}