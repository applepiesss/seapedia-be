package com.seapedia.be.dto;

public record AddressResponse(
        Long id,
        String recipientName,
        String phoneNumber,
        String fullAddress
) {}