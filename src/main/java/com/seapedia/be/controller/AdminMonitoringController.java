package com.seapedia.be.controller;

import com.seapedia.be.dto.AdminMonitoringResponse;
import com.seapedia.be.service.AdminMonitoringService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/monitoring")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).ADMIN)")
public class AdminMonitoringController {

    private final AdminMonitoringService monitoringService;

    public AdminMonitoringController(AdminMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping("/summary")
    public AdminMonitoringResponse getSummary() {
        return monitoringService.getSummary();
    }
}
