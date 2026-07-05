package com.seapedia.be.service;

import com.seapedia.be.enums.DeliveryMethod;
import com.seapedia.be.enums.OrderStatus;
import com.seapedia.be.enums.WalletTransactionType;
import com.seapedia.be.model.*;
import com.seapedia.be.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class OverdueService {

    private final CustomerOrderRepository orderRepository;
    private final BuyerWalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final ProductRepository productRepository;

    public OverdueService(
            CustomerOrderRepository orderRepository,
            BuyerWalletRepository walletRepository,
            WalletTransactionRepository walletTransactionRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void processOverdueOrders() {
        // Find all active orders
        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.SEDANG_DIKEMAS,
                OrderStatus.MENUNGGU_PENGIRIM,
                OrderStatus.SEDANG_DIKIRIM
        );
        List<CustomerOrder> activeOrders = orderRepository.findByStatusIn(activeStatuses);
        LocalDateTime now = LocalDateTime.now();

        for (CustomerOrder order : activeOrders) {
            if (order.getCreatedAt() == null || order.getDeliveryMethod() == null) continue;

            long hoursElapsed = ChronoUnit.HOURS.between(order.getCreatedAt(), now);
            long maxHours = getMaxSLAHours(order.getDeliveryMethod());

            if (hoursElapsed > maxHours) {
                // Order is overdue!
                handleOverdueOrder(order);
            }
        }
    }

    @Transactional
    public void simulateNextDay() {
        // Shift createdAt back by 24 hours for all active orders to simulate time passing
        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.SEDANG_DIKEMAS,
                OrderStatus.MENUNGGU_PENGIRIM,
                OrderStatus.SEDANG_DIKIRIM
        );
        List<CustomerOrder> activeOrders = orderRepository.findByStatusIn(activeStatuses);
        
        for (CustomerOrder order : activeOrders) {
            if (order.getCreatedAt() != null) {
                order.setCreatedAt(order.getCreatedAt().minusDays(1));
                orderRepository.save(order);
            }
        }
        
        // Immediately check overdue
        processOverdueOrders();
    }

    private void handleOverdueOrder(CustomerOrder order) {
        // Change status
        order.setStatus(OrderStatus.DIKEMBALIKAN);
        order.getStatusHistory().add(OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.DIKEMBALIKAN)
                .build());

        // 1. Refund the buyer
        BuyerWallet wallet = walletRepository.findByBuyer(order.getBuyer())
                .orElseGet(() -> {
                    BuyerWallet newWallet = BuyerWallet.builder()
                            .buyer(order.getBuyer())
                            .balance(java.math.BigDecimal.ZERO)
                            .build();
                    return walletRepository.save(newWallet);
                });
        wallet.setBalance(wallet.getBalance().add(order.getFinalTotal()));
        walletRepository.save(wallet);

        // Record refund transaction
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .type(WalletTransactionType.REFUND)
                .amount(order.getFinalTotal())
                .description("Refund for overdue order #" + order.getId())
                .build();
        walletTransactionRepository.save(transaction);

        // 2. Restore Product Stocks
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }
    }

    private long getMaxSLAHours(DeliveryMethod method) {
        return switch (method) {
            case INSTANT -> 24; // 1 day
            case NEXT_DAY -> 48; // 2 days
            case REGULAR -> 120; // 5 days
        };
    }
}
