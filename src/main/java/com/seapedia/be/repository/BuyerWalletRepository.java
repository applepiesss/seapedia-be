package com.seapedia.be.repository;

import com.seapedia.be.model.BuyerWallet;
import com.seapedia.be.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyerWalletRepository extends JpaRepository<BuyerWallet, Long> {
    Optional<BuyerWallet> findByBuyer(User buyer);
}