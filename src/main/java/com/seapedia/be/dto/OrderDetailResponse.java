package com.seapedia.be.dto;

import com.seapedia.be.enums.DeliveryMethod;
import com.seapedia.be.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderDetailResponse(
        Long id,
        String storeName,
        DeliveryMethod deliveryMethod,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal deliveryFee,
        BigDecimal ppn,
        BigDecimal finalTotal,
        List<OrderItemResponse> items,
        List<OrderStatusHistoryResponse> statusHistory
) {}