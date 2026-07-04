package com.seapedia.be.controller;

import com.seapedia.be.dto.ProfileResponse;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ProfileResponse me(@AuthenticationPrincipal AuthenticatedUser user) {
        return userService.getProfile(user.username(), user.activeRole());
    }

    @GetMapping("/buyer/dashboard")
    @PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).BUYER)")
    public String buyerDashboard() {
        return "Buyer dashboard placeholder";
    }

    @GetMapping("/seller/dashboard")
    @PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).SELLER)")
    public String sellerDashboard() {
        return "Seller dashboard placeholder";
    }

    @GetMapping("/driver/dashboard")
    @PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).DRIVER)")
    public String driverDashboard() {
        return "Driver dashboard placeholder";
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).ADMIN)")
    public String adminDashboard() {
        return "Admin dashboard placeholder";
    }
}