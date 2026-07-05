package com.seapedia.be.dto;

import com.seapedia.be.enums.DeliveryMethod;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
    @NotNull DeliveryMethod deliveryMethod
) {}