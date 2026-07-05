package com.seapedia.be.controller;

import com.seapedia.be.dto.OrderDetailResponse;
import com.seapedia.be.dto.OrderResponse;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/orders")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).BUYER)")
public class BuyerOrderController {

    private final OrderService orderService;

    public BuyerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> getOrders(@AuthenticationPrincipal AuthenticatedUser user) {
        return orderService.getBuyerOrders(user.username());
    }

    @GetMapping("/{orderId}")
    public OrderDetailResponse getOrder(@AuthenticationPrincipal AuthenticatedUser user,
                                        @PathVariable Long orderId) {
        return orderService.getBuyerOrder(user.username(), orderId);
    }
}
