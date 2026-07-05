package com.seapedia.be.dto;

import com.seapedia.be.enums.DeliveryMethod;

import java.math.BigDecimal;

public record CheckoutSummaryResponse(
        BigDecimal subtotal,
        BigDecimal deliveryFee,
        BigDecimal ppn,
        BigDecimal finalTotal,
        DeliveryMethod deliveryMethod
) {}