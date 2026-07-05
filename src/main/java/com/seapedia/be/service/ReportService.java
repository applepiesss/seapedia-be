package com.seapedia.be.service;

import com.seapedia.be.dto.BuyerReportResponse;
import com.seapedia.be.dto.OrderResponse;
import com.seapedia.be.dto.SellerReportResponse;
import com.seapedia.be.enums.OrderStatus;
import com.seapedia.be.model.CustomerOrder;
import com.seapedia.be.model.SellerStore;
import com.seapedia.be.model.User;
import com.seapedia.be.repository.CustomerOrderRepository;
import com.seapedia.be.repository.SellerStoreRepository;
import com.seapedia.be.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportService {

    private final CustomerOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SellerStoreRepository storeRepository;

    public ReportService(CustomerOrderRepository orderRepository,
                         UserRepository userRepository,
                         SellerStoreRepository storeRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional(readOnly = true)
    public BuyerReportResponse getBuyerReport(String username) {
        User buyer = getUser(username);
        List<CustomerOrder> orders = orderRepository.findByBuyerOrderByCreatedAtDesc(buyer);

        BigDecimal totalSpent = BigDecimal.ZERO;
        for (CustomerOrder order : orders) {
            if (order.getStatus() != OrderStatus.DIKEMBALIKAN) {
                totalSpent = totalSpent.add(order.getFinalTotal());
            }
        }

        List<OrderResponse> orderResponses = orders.stream().map(order -> new OrderResponse(
                order.getId(),
                order.getStore().getStoreName(),
                order.getStatus(),
                order.getFinalTotal(),
                order.getCreatedAt()
        )).toList();

        return new BuyerReportResponse(totalSpent, orderResponses);
    }

    @Transactional(readOnly = true)
    public SellerReportResponse getSellerReport(String username) {
        User sellerUser = getUser(username);
        SellerStore store = storeRepository.findByOwner(sellerUser)
                .orElseThrow(() -> new IllegalArgumentException("Seller store not found"));

        List<CustomerOrder> orders = orderRepository.findByStoreOrderByCreatedAtDesc(store);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (CustomerOrder order : orders) {
            if (order.getStatus() != OrderStatus.DIKEMBALIKAN) {
                // Revenue for seller is usually the item subtotal, but to keep it simple we can use subtotal.
                totalRevenue = totalRevenue.add(order.getSubtotal());
            }
        }

        List<OrderResponse> orderResponses = orders.stream().map(order -> new OrderResponse(
                order.getId(),
                order.getStore().getStoreName(),
                order.getStatus(),
                order.getFinalTotal(),
                order.getCreatedAt()
        )).toList();

        return new SellerReportResponse(totalRevenue, orderResponses);
    }
}
