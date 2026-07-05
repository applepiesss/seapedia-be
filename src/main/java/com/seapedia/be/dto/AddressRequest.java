package com.seapedia.be.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank String recipientName,
        @NotBlank String phoneNumber,
        @NotBlank String fullAddress
) {}