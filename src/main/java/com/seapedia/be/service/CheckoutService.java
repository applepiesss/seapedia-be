package com.seapedia.be.service;

import com.seapedia.be.dto.CheckoutRequest;
import com.seapedia.be.dto.CheckoutSummaryResponse;
import com.seapedia.be.enums.DeliveryMethod;
import com.seapedia.be.enums.OrderStatus;
import com.seapedia.be.enums.WalletTransactionType;
import com.seapedia.be.model.*;
import com.seapedia.be.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final BuyerWalletRepository walletRepository;
    private final CustomerOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public CheckoutService(CartRepository cartRepository,
                           BuyerWalletRepository walletRepository,
                           CustomerOrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           OrderStatusHistoryRepository statusHistoryRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           WalletTransactionRepository walletTransactionRepository) {
        this.cartRepository = cartRepository;
        this.walletRepository = walletRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            subtotal = subtotal.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return subtotal;
    }

    @Transactional(readOnly = true)
    public CheckoutSummaryResponse getCheckoutSummary(String username, DeliveryMethod deliveryMethod) {
        User buyer = getUser(username);
        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal deliveryFee = deliveryMethod.getFee();
        BigDecimal ppn = subtotal.multiply(new BigDecimal("0.12"));
        BigDecimal finalTotal = subtotal.add(deliveryFee).add(ppn);

        return new CheckoutSummaryResponse(subtotal, deliveryFee, ppn, finalTotal, deliveryMethod);
    }

    @Transactional
    public void checkout(String username, CheckoutRequest request) {
        User buyer = getUser(username);
        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BuyerWallet wallet = walletRepository.findByBuyer(buyer)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal deliveryFee = request.deliveryMethod().getFee();
        BigDecimal ppn = subtotal.multiply(new BigDecimal("0.12"));
        BigDecimal finalTotal = subtotal.add(deliveryFee).add(ppn);

        if (wallet.getBalance().compareTo(finalTotal) < 0) {
            throw new IllegalArgumentException("Insufficient wallet balance");
        }

        // Deduct stock
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for " + product.getProductName());
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        // Deduct wallet balance
        wallet.setBalance(wallet.getBalance().subtract(finalTotal));
        walletRepository.save(wallet);

        // Record wallet transaction
        walletTransactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .type(WalletTransactionType.CHECKOUT_PAYMENT)
                .amount(finalTotal.negate())
                .description("Checkout payment for order")
                .build());

        // Create order
        CustomerOrder order = CustomerOrder.builder()
                .buyer(buyer)
                .store(cart.getItems().get(0).getProduct().getStore())
                .deliveryMethod(request.deliveryMethod())
                .status(OrderStatus.SEDANG_DIKEMAS)
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .ppn(ppn)
                .finalTotal(finalTotal)
                .build();
        order = orderRepository.save(order);

        // Save order items
        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(item.getProduct())
                    .productName(item.getProduct().getProductName())
                    .price(item.getProduct().getPrice())
                    .quantity(item.getQuantity())
                    .build();
            orderItemRepository.save(orderItem);
        }

        // Save order status history
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.SEDANG_DIKEMAS)
                .build();
        statusHistoryRepository.save(history);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
