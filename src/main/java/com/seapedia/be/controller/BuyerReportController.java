package com.seapedia.be.controller;

import com.seapedia.be.dto.BuyerReportResponse;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/buyer/reports")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).BUYER)")
public class BuyerReportController {

    private final ReportService reportService;

    public BuyerReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/spending")
    public BuyerReportResponse getSpendingReport(@AuthenticationPrincipal AuthenticatedUser user) {
        return reportService.getBuyerReport(user.username());
    }
}
