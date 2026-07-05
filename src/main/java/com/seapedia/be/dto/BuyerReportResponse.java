package com.seapedia.be.dto;

import java.math.BigDecimal;
import java.util.List;

public record BuyerReportResponse(
        BigDecimal totalSpent,
        List<OrderResponse> orders
) {}
