package com.seapedia.be.controller;

import com.seapedia.be.dto.ProductResponse;
import com.seapedia.be.dto.StoreResponse;
import com.seapedia.be.service.PublicCatalogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicCatalogController {
    private final PublicCatalogService catalogService;

    public PublicCatalogController(PublicCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/products")
    public List<ProductResponse> listProducts() {
        return catalogService.listProducts();
    }

    @GetMapping("/products/{productId}")
    public ProductResponse getProduct(@PathVariable Long productId) {
        return catalogService.getProduct(productId);
    }

    @GetMapping("/stores/{storeId}")
    public StoreResponse getStore(@PathVariable Long storeId) {
        return catalogService.getStore(storeId);
    }
}