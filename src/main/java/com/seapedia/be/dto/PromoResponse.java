package com.seapedia.be.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromoResponse(
        Long id,
        String code,
        BigDecimal discountPercent,
        LocalDateTime expiryDate
) {}
