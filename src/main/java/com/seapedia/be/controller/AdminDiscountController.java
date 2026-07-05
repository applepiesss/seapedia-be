package com.seapedia.be.controller;

import com.seapedia.be.dto.PromoRequest;
import com.seapedia.be.dto.PromoResponse;
import com.seapedia.be.dto.VoucherRequest;
import com.seapedia.be.dto.VoucherResponse;
import com.seapedia.be.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/discounts")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).ADMIN)")
public class AdminDiscountController {
    
    private final DiscountService discountService;
    
    public AdminDiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }
    
    @PostMapping("/vouchers")
    public VoucherResponse createVoucher(@Valid @RequestBody VoucherRequest request) {
        return discountService.createVoucher(request);
    }
    
    @GetMapping("/vouchers")
    public List<VoucherResponse> getVouchers() {
        return discountService.getVouchers();
    }
    
    @PostMapping("/promos")
    public PromoResponse createPromo(@Valid @RequestBody PromoRequest request) {
        return discountService.createPromo(request);
    }
    
    @GetMapping("/promos")
    public List<PromoResponse> getPromos() {
        return discountService.getPromos();
    }
}
