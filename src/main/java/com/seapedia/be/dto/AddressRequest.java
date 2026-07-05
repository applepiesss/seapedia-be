package com.seapedia.be.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank String recipientName,
        @NotBlank @jakarta.validation.constraints.Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format") String phoneNumber,
        @NotBlank String fullAddress
) {}