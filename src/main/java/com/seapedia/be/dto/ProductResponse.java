package com.seapedia.be.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String productName,
        String description,
        BigDecimal price,
        Integer stock,
        Long storeId,
        String storeName,
        String sellerUsername
) {}