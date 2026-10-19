package com.goon.notificationservice.repository;

import com.goon.notificationservice.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {
}
