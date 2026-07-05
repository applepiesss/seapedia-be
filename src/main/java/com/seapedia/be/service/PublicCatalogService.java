package com.seapedia.be.service;

import com.seapedia.be.dto.ProductResponse;
import com.seapedia.be.dto.StoreResponse;
import com.seapedia.be.model.Product;
import com.seapedia.be.model.SellerStore;
import com.seapedia.be.repository.ProductRepository;
import com.seapedia.be.repository.SellerStoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicCatalogService {
    private final ProductRepository productRepository;
    private final SellerStoreRepository storeRepository;

    public PublicCatalogService(ProductRepository productRepository, SellerStoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

    public List<ProductResponse> listProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return toProductResponse(product);
    }

    public StoreResponse getStore(Long storeId) {
        SellerStore store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        return new StoreResponse(
                store.getId(),
                store.getStoreName(),
                store.getOwner().getUsername()
        );
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getStore().getId(),
                product.getStore().getStoreName(),
                product.getStore().getOwner().getUsername()
        );
    }
}