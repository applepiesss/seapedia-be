package com.seapedia.be.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromoRequest(
        @NotBlank(message = "Code is required")
        String code,
        
        @NotNull(message = "Discount percent is required")
        @Min(value = 1, message = "Discount percent must be at least 1")
        @Max(value = 100, message = "Discount percent cannot exceed 100")
        BigDecimal discountPercent,
        
        @NotNull(message = "Expiry date is required")
        @Future(message = "Expiry date must be in the future")
        LocalDateTime expiryDate
) {}
