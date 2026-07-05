package com.seapedia.be.service;

import com.seapedia.be.dto.OrderDetailResponse;
import com.seapedia.be.dto.OrderItemResponse;
import com.seapedia.be.dto.OrderResponse;
import com.seapedia.be.dto.OrderStatusHistoryResponse;
import com.seapedia.be.model.CustomerOrder;
import com.seapedia.be.model.SellerStore;
import com.seapedia.be.model.User;
import com.seapedia.be.repository.CustomerOrderRepository;
import com.seapedia.be.repository.SellerStoreRepository;
import com.seapedia.be.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SellerStoreRepository storeRepository;

    public OrderService(CustomerOrderRepository orderRepository,
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
    public List<OrderResponse> getBuyerOrders(String username) {
        User buyer = getUser(username);
        List<CustomerOrder> orders = orderRepository.findByBuyerOrderByCreatedAtDesc(buyer);

        return orders.stream().map(order -> new OrderResponse(
                order.getId(),
                order.getStore().getStoreName(),
                order.getStatus(),
                order.getFinalTotal(),
                order.getCreatedAt()
        )).toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getBuyerOrder(String username, Long orderId) {
        User buyer = getUser(username);
        CustomerOrder order = orderRepository.findById(orderId)
                .filter(o -> o.getBuyer().getId().equals(buyer.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Order not found or unauthorized"));

        return mapToOrderDetailResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getSellerOrders(String username) {
        User sellerUser = getUser(username);
        SellerStore store = storeRepository.findByOwner(sellerUser)
                .orElseThrow(() -> new IllegalArgumentException("Seller store not found"));

        List<CustomerOrder> orders = orderRepository.findByStoreOrderByCreatedAtDesc(store);

        return orders.stream().map(order -> new OrderResponse(
                order.getId(),
                order.getStore().getStoreName(),
                order.getStatus(),
                order.getFinalTotal(),
                order.getCreatedAt()
        )).toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getSellerOrder(String username, Long orderId) {
        User sellerUser = getUser(username);
        SellerStore store = storeRepository.findByOwner(sellerUser)
                .orElseThrow(() -> new IllegalArgumentException("Seller store not found"));

        CustomerOrder order = orderRepository.findById(orderId)
                .filter(o -> o.getStore().getId().equals(store.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Order not found or unauthorized"));

        return mapToOrderDetailResponse(order);
    }

    @Transactional
    public OrderDetailResponse processOrder(String username, Long orderId) {
        User sellerUser = getUser(username);
        SellerStore store = storeRepository.findByOwner(sellerUser)
                .orElseThrow(() -> new IllegalArgumentException("Seller store not found"));

        CustomerOrder order = orderRepository.findById(orderId)
                .filter(o -> o.getStore().getId().equals(store.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Order not found or unauthorized"));

        if (order.getStatus() != com.seapedia.be.enums.OrderStatus.SEDANG_DIKEMAS) {
            throw new IllegalArgumentException("Order cannot be processed from status: " + order.getStatus());
        }

        order.setStatus(com.seapedia.be.enums.OrderStatus.MENUNGGU_PENGIRIM);
        
        com.seapedia.be.model.OrderStatusHistory history = com.seapedia.be.model.OrderStatusHistory.builder()
                .order(order)
                .status(com.seapedia.be.enums.OrderStatus.MENUNGGU_PENGIRIM)
                .build();
        
        order.getStatusHistory().add(history);
        
        return mapToOrderDetailResponse(order);
    }

    private OrderDetailResponse mapToOrderDetailResponse(CustomerOrder order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity()
                )).toList();

        List<OrderStatusHistoryResponse> statusHistory = order.getStatusHistory().stream()
                .map(h -> new OrderStatusHistoryResponse(h.getStatus(), h.getCreatedAt()))
                .toList();

        return new OrderDetailResponse(
                order.getId(),
                order.getStore().getStoreName(),
                order.getDeliveryMethod(),
                order.getStatus(),
                order.getSubtotal(),
                order.getDiscount(),
                order.getDiscountType(),
                order.getDiscountCode(),
                order.getDeliveryFee(),
                order.getPpn(),
                order.getFinalTotal(),
                items,
                statusHistory
        );
    }
}
