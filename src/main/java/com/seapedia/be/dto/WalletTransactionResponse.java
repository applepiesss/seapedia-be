package com.seapedia.be.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletTransactionResponse(
    String type, 
    BigDecimal amount, 
    String description, 
    LocalDateTime createdAt
) {}