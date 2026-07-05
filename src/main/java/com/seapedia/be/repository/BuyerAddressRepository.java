package com.seapedia.be.repository;

import com.seapedia.be.model.BuyerAddress;
import com.seapedia.be.model.BuyerWallet;
import com.seapedia.be.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyerAddressRepository extends JpaRepository<BuyerAddress, Long> {
    Optional<BuyerAddress> findByBuyer(User buyer);
}