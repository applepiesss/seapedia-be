package com.seapedia.be.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse<OrderItemResponse>(
        List<OrderItemResponse> items,
        BigDecimal subtotal,
        String singleStoreRule
) {}