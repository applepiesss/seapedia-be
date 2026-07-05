package com.seapedia.be.dto;

import com.seapedia.be.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String storeName,
        OrderStatus status,
        BigDecimal finalTotal,
        LocalDateTime createdAt
) {}