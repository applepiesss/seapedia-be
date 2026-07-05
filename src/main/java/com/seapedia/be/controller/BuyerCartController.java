package com.seapedia.be.controller;

import com.seapedia.be.dto.CartItemRequest;
import com.seapedia.be.dto.CartResponse;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer/cart")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).BUYER)")
public class BuyerCartController {

    private final CartService cartService;

    public BuyerCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal AuthenticatedUser user) {
        return cartService.getCart(user.username());
    }

    @PostMapping("/items")
    public CartResponse addToCart(@AuthenticationPrincipal AuthenticatedUser user,
                                  @Valid @RequestBody CartItemRequest request) {
        return cartService.addToCart(user.username(), request);
    }

    @PutMapping("/items/{productId}")
    public CartResponse updateCartItem(@AuthenticationPrincipal AuthenticatedUser user,
                                       @PathVariable Long productId,
                                       @RequestParam Integer quantity) {
        return cartService.updateCartItem(user.username(), productId, quantity);
    }

    @DeleteMapping("/items/{productId}")
    public CartResponse removeFromCart(@AuthenticationPrincipal AuthenticatedUser user,
                                       @PathVariable Long productId) {
        return cartService.removeFromCart(user.username(), productId);
    }
}
