package com.seapedia.be.repository;

import com.seapedia.be.model.Product;
import com.seapedia.be.model.SellerStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStoreOrderByCreatedAtDesc(SellerStore store);

    List<Product> findAllByOrderByCreatedAtDesc();
}