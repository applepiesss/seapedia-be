package com.seapedia.be.dto;

import com.seapedia.be.enums.DeliveryMethod;

import com.seapedia.be.enums.DiscountType;
import java.math.BigDecimal;

public record CheckoutSummaryResponse(
        BigDecimal subtotal,
        BigDecimal discount,
        DiscountType discountType,
        BigDecimal deliveryFee,
        BigDecimal ppn,
        BigDecimal finalTotal,
        DeliveryMethod deliveryMethod
) {}