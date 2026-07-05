package com.seapedia.be.dto;

import com.seapedia.be.enums.DeliveryJobStatus;
import com.seapedia.be.enums.DeliveryMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DriverJobResponse(
        Long id,
        Long orderId,
        String storeName,
        DeliveryMethod deliveryMethod,
        BigDecimal earning,
        DeliveryJobStatus status,
        LocalDateTime createdAt,
        LocalDateTime takenAt,
        LocalDateTime completedAt
) {}
