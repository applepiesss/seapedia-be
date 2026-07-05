package com.seapedia.be.repository;

import com.seapedia.be.model.SellerStore;
import com.seapedia.be.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerStoreRepository extends JpaRepository<SellerStore, Long> {
    Optional<SellerStore> findByOwner(User owner);

    boolean existsByStoreNameIgnoreCase(String storeName);

    boolean existsByStoreNameIgnoreCaseAndOwnerNot(String storeName, User owner);
}