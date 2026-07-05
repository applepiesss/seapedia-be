package com.seapedia.be.dto;

import java.math.BigDecimal;
import java.util.List;

public record DriverDashboardResponse(
        BigDecimal totalEarnings,
        DriverJobResponse activeJob,
        List<DriverJobResponse> jobHistory
) {}
