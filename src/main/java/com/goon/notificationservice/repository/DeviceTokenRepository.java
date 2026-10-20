package com.goon.notificationservice.repository;

import com.goon.notificationservice.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {

    @Modifying(clearAutomatically = true,flushAutomatically = true)
    @Query("""
        UPDATE DeviceToken 
        SET fcmToken = :fcmToken
        WHERE userId = :userId
        """)
    int upsertRecord(@Param("userId") UUID userId,@Param("fcmToken") String fmcToken);
}
