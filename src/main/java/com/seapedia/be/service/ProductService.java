package com.seapedia.be.service;

import com.seapedia.be.dto.ProductRequest;
import com.seapedia.be.dto.ProductResponse;
import com.seapedia.be.model.Product;
import com.seapedia.be.model.SellerStore;
import com.seapedia.be.model.User;
import com.seapedia.be.repository.ProductRepository;
import com.seapedia.be.repository.SellerStoreRepository;
import com.seapedia.be.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final SellerStoreRepository storeRepository;
    private final UserRepository userRepository;

    public ProductService(
            ProductRepository productRepository,
            SellerStoreRepository storeRepository,
            UserRepository userRepository
    ) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    public List<ProductResponse> getMyProducts(String username) {
        SellerStore store = getOwnedStore(username);

        return productRepository.findByStoreOrderByCreatedAtDesc(store)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse createProduct(String username, ProductRequest request) {
        SellerStore store = getOwnedStore(username);

        Product product = Product.builder()
                .productName(request.productName().trim())
                .description(request.description().trim())
                .price(request.price())
                .stock(request.stock())
                .store(store)
                .build();

        return toResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(String username, Long productId, ProductRequest request) {
        SellerStore store = getOwnedStore(username);
        Product product = getOwnedProduct(productId, store);

        product.setProductName(request.productName().trim());
        product.setDescription(request.description().trim());
        product.setPrice(request.price());
        product.setStock(request.stock());

        return toResponse(productRepository.save(product));
    }

    public void deleteProduct(String username, Long productId) {
        SellerStore store = getOwnedStore(username);
        Product product = getOwnedProduct(productId, store);

        productRepository.delete(product);
    }

    private SellerStore getOwnedStore(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return storeRepository.findByOwner(owner)
                .orElseThrow(() -> new IllegalArgumentException("Create your seller store first"));
    }

    private Product getOwnedProduct(Long productId, SellerStore store) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getStore().getId().equals(store.getId())) {
            throw new IllegalArgumentException("Seller may only manage their own products");
        }

        return product;
    }

    private ProductResponse toResponse(Product product) {
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