package com.seapedia.be.service;

import com.seapedia.be.dto.StoreRequest;
import com.seapedia.be.dto.StoreResponse;
import com.seapedia.be.enums.Role;
import com.seapedia.be.model.SellerStore;
import com.seapedia.be.model.User;
import com.seapedia.be.repository.SellerStoreRepository;
import com.seapedia.be.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SellerStoreService {
    private final SellerStoreRepository storeRepository;
    private final UserRepository userRepository;

    public SellerStoreService(SellerStoreRepository storeRepository, UserRepository userRepository) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    public StoreResponse getMyStore(String username) {
        User owner = getSeller(username);

        SellerStore store = storeRepository.findByOwner(owner)
                .orElseThrow(() -> new IllegalArgumentException("Seller store not found"));

        return toResponse(store);
    }

    public StoreResponse createOrUpdateMyStore(String username, StoreRequest request) {
        User owner = getSeller(username);
        String storeName = request.storeName().trim();

        SellerStore store = storeRepository.findByOwner(owner).orElse(null);

        if (store == null) {
            if (storeRepository.existsByStoreNameIgnoreCase(storeName)) {
                throw new IllegalArgumentException("Store name is already used");
            }

            store = SellerStore.builder()
                    .storeName(storeName)
                    .owner(owner)
                    .build();
        } else {
            if (storeRepository.existsByStoreNameIgnoreCaseAndOwnerNot(storeName, owner)) {
                throw new IllegalArgumentException("Store name is already used");
            }

            store.setStoreName(storeName);
        }

        return toResponse(storeRepository.save(store));
    }

    private User getSeller(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.hasRole(Role.SELLER)) {
            throw new IllegalArgumentException("User does not own seller role");
        }

        return user;
    }

    private StoreResponse toResponse(SellerStore store) {
        return new StoreResponse(
                store.getId(),
                store.getStoreName(),
                store.getOwner().getUsername()
        );
    }
}