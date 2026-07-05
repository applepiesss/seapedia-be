package com.seapedia.be.controller;

import com.seapedia.be.service.OverdueService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/actions")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).ADMIN)")
public class AdminActionController {

    private final OverdueService overdueService;

    public AdminActionController(OverdueService overdueService) {
        this.overdueService = overdueService;
    }

    @PostMapping("/simulate-next-day")
    public org.springframework.http.ResponseEntity<Map<String, String>> simulateNextDay() {
        try {
            overdueService.simulateNextDay();
            return org.springframework.http.ResponseEntity.ok(Map.of("message", "Simulated passing of 1 day successfully. Overdue orders processed."));
        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.status(500).body(Map.of("message", "Backend Error: " + e.getClass().getSimpleName() + " - " + e.getMessage()));
        }
    }

    @PostMapping("/process-overdue")
    public org.springframework.http.ResponseEntity<Map<String, String>> processOverdue() {
        try {
            overdueService.processOverdueOrders();
            return org.springframework.http.ResponseEntity.ok(Map.of("message", "Checked and processed overdue orders based on current time."));
        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.status(500).body(Map.of("message", "Backend Error: " + e.getClass().getSimpleName() + " - " + e.getMessage()));
        }
    }
}
