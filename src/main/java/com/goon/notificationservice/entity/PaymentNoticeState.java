package com.goon.notificationservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity @Table(name = "payment_notice_state")
@Getter @Setter @NoArgsConstructor
public class PaymentNoticeState {
    @Id @Column(name = "ride_id")
    private UUID rideId;
    @Column(nullable = false)
    private long revision;
    @Column(nullable = false, length = 16)
    private String status = "PENDING";
}

