package com.seapedia.be.controller;

import com.seapedia.be.dto.ProductRequest;
import com.seapedia.be.dto.ProductResponse;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/products")
@PreAuthorize("@activeRoleAuthorization.hasActiveRole(authentication, T(com.seapedia.be.enums.Role).SELLER)")
public class SellerProductController {
    private final ProductService productService;

    public SellerProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getMyProducts(@AuthenticationPrincipal AuthenticatedUser user) {
        return productService.getMyProducts(user.username());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ProductRequest request
    ) {
        return productService.createProduct(user.username(), request);
    }

    @PutMapping("/{productId}")
    public ProductResponse updateProduct(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request
    ) {
        return productService.updateProduct(user.username(), productId, request);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long productId
    ) {
        productService.deleteProduct(user.username(), productId);
    }
}