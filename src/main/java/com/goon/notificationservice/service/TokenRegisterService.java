package com.goon.notificationservice.service;

import com.goon.notificationservice.entity.DeviceToken;
import com.goon.notificationservice.repository.DeviceTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenRegisterService {
    private final DeviceTokenRepository deviceTokenRepository;


    @Transactional   //for custom queries such as update,delete,insert , better to keep transactional on method that is calling that query method  .
    public void registerToken(UUID userId, String fcmToken) {

        if (fcmToken == null || fcmToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid FCM token");
        }

        if (deviceTokenRepository.findById(userId).isEmpty()) {
            createRecord(userId, fcmToken);
        } else {
            updateRecord(userId, fcmToken);
        }
    }



    private void createRecord(UUID userId, String fcmToken){
        DeviceToken deviceToken = DeviceToken.builder()
                .userId(userId)
                .fcmToken(fcmToken)
                .build();
        deviceTokenRepository.save(deviceToken);
    }

    private void updateRecord(UUID userId, String fcmToken){

       if(deviceTokenRepository.upsertRecord(userId,fcmToken)!=1){
           throw new ResponseStatusException(
                   HttpStatus.CONFLICT, "Token update failed");
       }
    }
}
