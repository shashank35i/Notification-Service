package com.goon.notificationservice.controller;


import com.goon.notificationservice.dto.RegisterToken;
import com.goon.notificationservice.service.TokenRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenRegisterController {

    private final TokenRegisterService tokenRegisterService;

    @PostMapping("/register_fcm_token")
    public ResponseEntity<Void> registerFcmToken(
            @RequestHeader("X-user-Id")UUID userId,
            @RequestBody RegisterToken token
            ){

        tokenRegisterService.registerToken(userId,token.fcmToken());

        return ResponseEntity.noContent()
                .build();
    }
}
