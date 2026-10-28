package com.goon.notificationservice.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity @Table(name = "payment_notice_state")
@Getter @Setter @NoArgsConstructor
public class PaymentNoticeState {
    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "ride_id", columnDefinition = "BINARY(16)")
    private UUID rideId;
    @Column(nullable = false)
    private long revision;
    @Column(nullable = false, length = 16)
    private String status = "PENDING";
}

