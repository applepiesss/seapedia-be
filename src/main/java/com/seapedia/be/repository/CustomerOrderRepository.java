package com.seapedia.be.repository;

import com.seapedia.be.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByBuyerOrderByCreatedAtDesc(User buyer);
    List<CustomerOrder> findByStoreOrderByCreatedAtDesc(SellerStore store);
}