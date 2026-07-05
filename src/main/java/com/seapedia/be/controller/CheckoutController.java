package com.seapedia.be.controller;

import com.seapedia.be.dto.CheckoutRequest;
import com.seapedia.be.dto.CheckoutSummaryResponse;
import com.seapedia.be.enums.DeliveryMethod;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer/checkout")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).BUYER)")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping("/summary")
    public CheckoutSummaryResponse getCheckoutSummary(@AuthenticationPrincipal AuthenticatedUser user,
                                                      @RequestParam DeliveryMethod deliveryMethod) {
        return checkoutService.getCheckoutSummary(user.username(), deliveryMethod);
    }

    @PostMapping
    public void checkout(@AuthenticationPrincipal AuthenticatedUser user,
                         @Valid @RequestBody CheckoutRequest request) {
        checkoutService.checkout(user.username(), request);
    }
}
