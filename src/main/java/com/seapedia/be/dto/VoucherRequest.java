package com.seapedia.be.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VoucherRequest(
        @NotBlank(message = "Code is required")
        String code,
        
        @NotNull(message = "Discount amount is required")
        @Min(value = 1, message = "Discount amount must be positive")
        BigDecimal discountAmount,
        
        @NotNull(message = "Expiry date is required")
        @Future(message = "Expiry date must be in the future")
        LocalDateTime expiryDate,
        
        @NotNull(message = "Remaining usage is required")
        @Min(value = 1, message = "Remaining usage must be at least 1")
        Integer remainingUsage
) {}
