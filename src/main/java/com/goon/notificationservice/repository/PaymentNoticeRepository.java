package com.goon.notificationservice.repository;
import com.goon.notificationservice.entity.PaymentNoticeState;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface PaymentNoticeRepository extends JpaRepository<PaymentNoticeState, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from PaymentNoticeState n where n.rideId = :rideId")
    Optional<PaymentNoticeState> findForUpdate(@Param("rideId") UUID rideId);
}

