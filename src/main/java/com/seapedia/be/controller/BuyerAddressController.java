package com.seapedia.be.controller;

import com.seapedia.be.dto.AddressRequest;
import com.seapedia.be.dto.AddressResponse;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.BuyerAddressService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer/address")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).BUYER)")
public class BuyerAddressController {

    private final BuyerAddressService addressService;

    public BuyerAddressController(BuyerAddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public AddressResponse getAddress(@AuthenticationPrincipal AuthenticatedUser user) {
        return addressService.getAddress(user.username());
    }

    @PutMapping
    public AddressResponse saveAddress(@AuthenticationPrincipal AuthenticatedUser user,
                                       @Valid @RequestBody AddressRequest request) {
        return addressService.saveAddress(user.username(), request);
    }
}
