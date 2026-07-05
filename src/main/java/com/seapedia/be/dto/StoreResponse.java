package com.seapedia.be.dto;

public record StoreResponse(
        Long id,
        String storeName,
        String ownerUsername
) {}