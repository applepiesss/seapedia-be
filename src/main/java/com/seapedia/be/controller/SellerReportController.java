package com.seapedia.be.controller;

import com.seapedia.be.dto.SellerReportResponse;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller/reports")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).SELLER)")
public class SellerReportController {

    private final ReportService reportService;

    public SellerReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/income")
    public SellerReportResponse getIncomeReport(@AuthenticationPrincipal AuthenticatedUser user) {
        return reportService.getSellerReport(user.username());
    }
}
