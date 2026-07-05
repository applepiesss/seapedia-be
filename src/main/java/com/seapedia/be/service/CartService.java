package com.seapedia.be.service;

import com.seapedia.be.dto.CartItemRequest;
import com.seapedia.be.dto.CartResponse;
import com.seapedia.be.dto.OrderItemResponse;
import com.seapedia.be.model.Cart;
import com.seapedia.be.model.CartItem;
import com.seapedia.be.model.Product;
import com.seapedia.be.model.User;
import com.seapedia.be.repository.CartItemRepository;
import com.seapedia.be.repository.CartRepository;
import com.seapedia.be.repository.ProductRepository;
import com.seapedia.be.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public CartResponse addToCart(String username, CartItemRequest request) {
        User buyer = getUser(username);
        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseGet(() -> cartRepository.save(Cart.builder().buyer(buyer).build()));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!cart.getItems().isEmpty()) {
            Long existingStoreId = cart.getItems().get(0).getProduct().getStore().getId();
            Long incomingStoreId = product.getStore().getId();

            if (!existingStoreId.equals(incomingStoreId)) {
                throw new IllegalArgumentException(
                    "Single-store checkout rule: cart may only contain products from one store"
                );
            }
        }

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseGet(() -> CartItem.builder().cart(cart).product(product).quantity(0).build());

        item.setQuantity(item.getQuantity() + request.quantity());
        cartItemRepository.save(item);

        if (!cart.getItems().contains(item)) {
            cart.getItems().add(item);
        }

        return getCart(username);
    }

    @Transactional
    public CartResponse updateCartItem(String username, Long productId, Integer quantity) {
        User buyer = getUser(username);
        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return getCart(username);
    }

    @Transactional
    public CartResponse removeFromCart(String username, Long productId) {
        return updateCartItem(username, productId, 0);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String username) {
        User buyer = getUser(username);
        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseGet(() -> Cart.builder().buyer(buyer).items(new ArrayList<>()).build());

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItemResponse> items = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            BigDecimal price = product.getPrice();
            Integer qty = item.getQuantity();
            subtotal = subtotal.add(price.multiply(BigDecimal.valueOf(qty)));

            items.add(new OrderItemResponse(
                    product.getId(),
                    product.getProductName(),
                    price,
                    qty
            ));
        }

        return new CartResponse(
                items,
                subtotal,
                "One cart may only contain products from one store."
        );
    }
}
