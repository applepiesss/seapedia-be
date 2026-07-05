package com.seapedia.be.dto;

public record AdminMonitoringResponse(
        long totalUsers,
        long totalStores,
        long totalProducts,
        long totalOrders,
        long totalVouchers,
        long totalPromos,
        long totalDeliveryJobs,
        long totalOverdueOrdersReturned
) {}
