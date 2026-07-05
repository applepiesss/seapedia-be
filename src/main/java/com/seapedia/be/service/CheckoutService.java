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
    private final VoucherRepository voucherRepository;
    private final PromoRepository promoRepository;

    public CheckoutService(CartRepository cartRepository,
                           BuyerWalletRepository walletRepository,
                           CustomerOrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           OrderStatusHistoryRepository statusHistoryRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           WalletTransactionRepository walletTransactionRepository,
                           VoucherRepository voucherRepository,
                           PromoRepository promoRepository) {
        this.cartRepository = cartRepository;
        this.walletRepository = walletRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.voucherRepository = voucherRepository;
        this.promoRepository = promoRepository;
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
    public CheckoutSummaryResponse getCheckoutSummary(String username, DeliveryMethod deliveryMethod, String discountCode) {
        User buyer = getUser(username);
        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal subtotal = calculateSubtotal(cart);
        
        BigDecimal discount = BigDecimal.ZERO;
        com.seapedia.be.enums.DiscountType discountType = com.seapedia.be.enums.DiscountType.NONE;
        
        if (discountCode != null && !discountCode.isBlank()) {
            String code = discountCode.toUpperCase();
            var voucherOpt = voucherRepository.findByCodeIgnoreCase(code);
            var promoOpt = promoRepository.findByCodeIgnoreCase(code);
            
            if (voucherOpt.isPresent()) {
                Voucher v = voucherOpt.get();
                if (v.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
                    throw new IllegalArgumentException("Voucher is expired");
                }
                if (v.getRemainingUsage() <= 0) {
                    throw new IllegalArgumentException("Voucher has no remaining usage");
                }
                discount = v.getDiscountAmount();
                discountType = com.seapedia.be.enums.DiscountType.VOUCHER;
            } else if (promoOpt.isPresent()) {
                Promo p = promoOpt.get();
                if (p.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
                    throw new IllegalArgumentException("Promo is expired");
                }
                discount = subtotal.multiply(p.getDiscountPercent()).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                discountType = com.seapedia.be.enums.DiscountType.PROMO;
            } else {
                throw new IllegalArgumentException("Invalid discount code");
            }
        }
        
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal; // discount cannot exceed subtotal
        }
        
        BigDecimal discountedSubtotal = subtotal.subtract(discount);
        BigDecimal deliveryFee = deliveryMethod.getFee();
        BigDecimal ppn = discountedSubtotal.multiply(new BigDecimal("0.12")).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal finalTotal = discountedSubtotal.add(deliveryFee).add(ppn);

        return new CheckoutSummaryResponse(subtotal, discount, discountType, deliveryFee, ppn, finalTotal, deliveryMethod);
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
        
        BigDecimal discount = BigDecimal.ZERO;
        com.seapedia.be.enums.DiscountType discountType = com.seapedia.be.enums.DiscountType.NONE;
        String discountCodeUsed = null;
        
        if (request.discountCode() != null && !request.discountCode().isBlank()) {
            String code = request.discountCode().toUpperCase();
            var voucherOpt = voucherRepository.findByCodeIgnoreCase(code);
            var promoOpt = promoRepository.findByCodeIgnoreCase(code);
            
            if (voucherOpt.isPresent()) {
                Voucher v = voucherOpt.get();
                if (v.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
                    throw new IllegalArgumentException("Voucher is expired");
                }
                if (v.getRemainingUsage() <= 0) {
                    throw new IllegalArgumentException("Voucher has no remaining usage");
                }
                discount = v.getDiscountAmount();
                discountType = com.seapedia.be.enums.DiscountType.VOUCHER;
                discountCodeUsed = code;
                
                // Decrement usage
                v.setRemainingUsage(v.getRemainingUsage() - 1);
                voucherRepository.save(v);
            } else if (promoOpt.isPresent()) {
                Promo p = promoOpt.get();
                if (p.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
                    throw new IllegalArgumentException("Promo is expired");
                }
                discount = subtotal.multiply(p.getDiscountPercent()).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                discountType = com.seapedia.be.enums.DiscountType.PROMO;
                discountCodeUsed = code;
            } else {
                throw new IllegalArgumentException("Invalid discount code");
            }
        }
        
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }
        
        BigDecimal discountedSubtotal = subtotal.subtract(discount);
        BigDecimal deliveryFee = request.deliveryMethod().getFee();
        BigDecimal ppn = discountedSubtotal.multiply(new BigDecimal("0.12")).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal finalTotal = discountedSubtotal.add(deliveryFee).add(ppn);

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
                .discount(discount)
                .discountType(discountType)
                .discountCode(discountCodeUsed)
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
