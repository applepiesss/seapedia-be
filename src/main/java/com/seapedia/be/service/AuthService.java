package com.seapedia.be.service;

import com.seapedia.be.dto.AuthResponse;
import com.seapedia.be.dto.ChooseRoleRequest;
import com.seapedia.be.dto.LoginRequest;
import com.seapedia.be.dto.RegisterRequest;
import com.seapedia.be.enums.Role;
import com.seapedia.be.model.User;
import com.seapedia.be.repository.UserRepository;
import com.seapedia.be.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        validateRoles(request.roles());

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .passwordHash(passwordEncoder.encode(request.password()))
                .roles(request.roles())
                .build();

        userRepository.save(user);

        return buildAuthResponse(user, defaultActiveRole(user.getRoles()));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return buildAuthResponse(user, defaultActiveRole(user.getRoles()));
    }

    public AuthResponse chooseActiveRole(String username, ChooseRoleRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Role activeRole = request.activeRole();

        if (!user.hasRole(activeRole)) {
            throw new IllegalArgumentException("User does not own this role");
        }

        return buildAuthResponse(user, activeRole);
    }

    private AuthResponse buildAuthResponse(User user, Role activeRole) {
        String token = jwtService.generateToken(user, activeRole);

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRoles(),
                activeRole,
                mustChooseRole(user.getRoles(), activeRole)
        );
    }

    private Role defaultActiveRole(Set<Role> roles) {
        if (roles.contains(Role.ADMIN)) {
            return Role.ADMIN;
        }

        Set<Role> nonAdminRoles = roles.stream()
                .filter(role -> role != Role.ADMIN)
                .collect(Collectors.toSet());

        if (nonAdminRoles.size() == 1) {
            return nonAdminRoles.iterator().next();
        }

        return null;
    }

    private boolean mustChooseRole(Set<Role> roles, Role activeRole) {
        long nonAdminCount = roles.stream()
                .filter(role -> role != Role.ADMIN)
                .count();

        return activeRole == null && nonAdminCount > 1;
    }

    private void validateRoles(Set<Role> roles) {
        if (roles.contains(Role.ADMIN) && roles.size() > 1) {
            throw new IllegalArgumentException("Admin role must be used separately from non-admin roles");
        }
    }
}