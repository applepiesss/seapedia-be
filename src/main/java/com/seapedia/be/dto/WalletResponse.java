package com.seapedia.be.dto;

import java.math.BigDecimal;
import java.util.List;

public record WalletResponse(
        BigDecimal balance,
        List<WalletTransactionResponse> transactions
    ) {}