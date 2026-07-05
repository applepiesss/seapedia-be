package com.seapedia.be.dto;

import com.seapedia.be.enums.OrderStatus;

import java.time.LocalDateTime;

public record OrderStatusHistoryResponse(
        OrderStatus status,
        LocalDateTime createdAt
) {}