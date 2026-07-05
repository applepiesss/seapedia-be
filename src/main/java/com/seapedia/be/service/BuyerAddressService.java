package com.seapedia.be.service;

import com.seapedia.be.dto.AddressRequest;
import com.seapedia.be.dto.AddressResponse;
import com.seapedia.be.model.BuyerAddress;
import com.seapedia.be.model.User;
import com.seapedia.be.repository.BuyerAddressRepository;
import com.seapedia.be.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuyerAddressService {

    private final BuyerAddressRepository addressRepository;
    private final UserRepository userRepository;

    public BuyerAddressService(BuyerAddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddress(String username) {
        User buyer = getUser(username);
        return addressRepository.findByBuyer(buyer)
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Transactional
    public AddressResponse saveAddress(String username, AddressRequest request) {
        User buyer = getUser(username);
        BuyerAddress address = addressRepository.findByBuyer(buyer)
                .orElseGet(() -> BuyerAddress.builder().buyer(buyer).build());

        address.setRecipientName(request.recipientName());
        address.setPhoneNumber(request.phoneNumber());
        address.setFullAddress(request.fullAddress());

        BuyerAddress saved = addressRepository.save(address);
        return mapToResponse(saved);
    }

    private AddressResponse mapToResponse(BuyerAddress address) {
        return new AddressResponse(
                address.getId(),
                address.getRecipientName(),
                address.getPhoneNumber(),
                address.getFullAddress()
        );
    }
}
