package com.seapedia.be.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VoucherResponse(
        Long id,
        String code,
        BigDecimal discountAmount,
        LocalDateTime expiryDate,
        Integer remainingUsage
) {}
