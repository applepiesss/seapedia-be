package com.seapedia.be.repository;

import com.seapedia.be.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByBuyerOrderByCreatedAtDesc(User buyer);
    List<CustomerOrder> findByStoreOrderByCreatedAtDesc(SellerStore store);
    long countByStatus(com.seapedia.be.enums.OrderStatus status);
    List<CustomerOrder> findByStatusIn(List<com.seapedia.be.enums.OrderStatus> statuses);
}