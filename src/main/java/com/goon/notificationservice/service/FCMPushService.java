package com.goon.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.goon.notificationservice.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FCMPushService {


    private final FirebaseMessaging firebaseMessaging;
    private final DeviceTokenRepository tokenRepository;
    public void sendNotification(
            UUID userId,String title,String body,
            Map<String,String> data
                         )  throws FirebaseMessagingException
        {
        var device = tokenRepository.findById(userId);
        if(device.isEmpty()){
            return;
        }

        Message message = Message.builder()
                .setToken(device.get().getFcmToken())
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .putAllData(data)
                .build();

        firebaseMessaging.send(message);
    }
}
