package com.seapedia.be.dto;

import java.math.BigDecimal;
import java.util.List;

public record SellerReportResponse(
        BigDecimal totalRevenue,
        List<OrderResponse> orders
) {}
