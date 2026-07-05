package com.seapedia.be.service;

import com.seapedia.be.dto.AdminMonitoringResponse;
import com.seapedia.be.enums.OrderStatus;
import com.seapedia.be.repository.*;
import org.springframework.stereotype.Service;

@Service
public class AdminMonitoringService {

    private final UserRepository userRepository;
    private final SellerStoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository orderRepository;
    private final VoucherRepository voucherRepository;
    private final PromoRepository promoRepository;
    private final DeliveryJobRepository deliveryJobRepository;

    public AdminMonitoringService(
            UserRepository userRepository,
            SellerStoreRepository storeRepository,
            ProductRepository productRepository,
            CustomerOrderRepository orderRepository,
            VoucherRepository voucherRepository,
            PromoRepository promoRepository,
            DeliveryJobRepository deliveryJobRepository) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.voucherRepository = voucherRepository;
        this.promoRepository = promoRepository;
        this.deliveryJobRepository = deliveryJobRepository;
    }

    public AdminMonitoringResponse getSummary() {
        return new AdminMonitoringResponse(
                userRepository.count(),
                storeRepository.count(),
                productRepository.count(),
                orderRepository.count(),
                voucherRepository.count(),
                promoRepository.count(),
                deliveryJobRepository.count(),
                orderRepository.countByStatus(OrderStatus.DIKEMBALIKAN)
        );
    }
}
