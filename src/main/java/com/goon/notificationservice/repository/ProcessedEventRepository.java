package com.goon.notificationservice.repository;

import com.goon.notificationservice.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, UUID> {
}