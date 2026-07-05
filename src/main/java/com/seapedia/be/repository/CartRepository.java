package com.seapedia.be.repository;

import com.seapedia.be.model.BuyerWallet;
import com.seapedia.be.model.Cart;
import com.seapedia.be.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByBuyer(User buyer);
}