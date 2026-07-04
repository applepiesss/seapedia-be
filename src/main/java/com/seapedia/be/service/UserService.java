package com.seapedia.be.service;

import com.seapedia.be.dto.FinancialSummaryPlaceholder;
import com.seapedia.be.dto.ProfileResponse;
import com.seapedia.be.enums.Role;
import com.seapedia.be.model.User;
import com.seapedia.be.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileResponse getProfile(String username, Role activeRole) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new ProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoles(),
                activeRole,
                new FinancialSummaryPlaceholder(
                        "Not implemented yet",
                        "Not implemented yet",
                        "Not implemented yet",
                        "Wallet balance, seller income, and driver earnings will be introduced in later levels."
                )
        );
    }
}