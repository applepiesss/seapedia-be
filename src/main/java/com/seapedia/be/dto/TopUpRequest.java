package com.seapedia.be.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TopUpRequest(
    @NotNull @DecimalMin("1.00") BigDecimal amount
) {}
