package com.seapedia.be.controller;

import com.seapedia.be.dto.StoreRequest;
import com.seapedia.be.dto.StoreResponse;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.SellerStoreService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/store")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).SELLER)")
public class SellerStoreController {
    private final SellerStoreService storeService;

    public SellerStoreController(SellerStoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public StoreResponse getMyStore(@AuthenticationPrincipal AuthenticatedUser user) {
        return storeService.getMyStore(user.username());
    }

    @PutMapping
    public StoreResponse createOrUpdateMyStore(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody StoreRequest request
    ) {
        return storeService.createOrUpdateMyStore(user.username(), request);
    }
}