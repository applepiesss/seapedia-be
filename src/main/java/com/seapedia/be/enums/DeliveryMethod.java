package com.seapedia.be.enums;

import java.math.BigDecimal;

public enum DeliveryMethod {
    INSTANT(new BigDecimal("20000")),
    NEXT_DAY(new BigDecimal("12000")),
    REGULAR(new BigDecimal("8000"));

    private final BigDecimal fee;

    DeliveryMethod(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFee() {
        return fee;
    }
}