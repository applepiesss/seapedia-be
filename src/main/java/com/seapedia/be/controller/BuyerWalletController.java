package com.seapedia.be.controller;

import com.seapedia.be.dto.TopUpRequest;
import com.seapedia.be.dto.WalletResponse;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.BuyerWalletService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer/wallet")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).BUYER)")
public class BuyerWalletController {

    private final BuyerWalletService walletService;

    public BuyerWalletController(BuyerWalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public WalletResponse getWallet(@AuthenticationPrincipal AuthenticatedUser user) {
        return walletService.getWallet(user.username());
    }

    @PostMapping("/top-up")
    public WalletResponse topUp(@AuthenticationPrincipal AuthenticatedUser user,
                                @Valid @RequestBody TopUpRequest request) {
        return walletService.topUp(user.username(), request);
    }
}
