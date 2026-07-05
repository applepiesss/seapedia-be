package com.seapedia.be.controller;

import com.seapedia.be.dto.DriverDashboardResponse;
import com.seapedia.be.dto.DriverJobResponse;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.DriverJobService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).DRIVER)")
public class DriverJobController {

    private final DriverJobService driverJobService;

    public DriverJobController(DriverJobService driverJobService) {
        this.driverJobService = driverJobService;
    }

    @GetMapping("/jobs/available")
    public List<DriverJobResponse> getAvailableJobs() {
        return driverJobService.getAvailableJobs();
    }

    @GetMapping("/jobs/{jobId}")
    public DriverJobResponse getJobDetails(@PathVariable Long jobId) {
        return driverJobService.getJobDetails(jobId);
    }

    @PostMapping("/jobs/{jobId}/take")
    public DriverJobResponse takeJob(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long jobId) {
        return driverJobService.takeJob(user.username(), jobId);
    }

    @PostMapping("/jobs/{jobId}/complete")
    public DriverJobResponse completeJob(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long jobId) {
        return driverJobService.completeJob(user.username(), jobId);
    }

    @GetMapping("/dashboard")
    public DriverDashboardResponse getDashboard(@AuthenticationPrincipal AuthenticatedUser user) {
        return driverJobService.getDashboard(user.username());
    }
}
