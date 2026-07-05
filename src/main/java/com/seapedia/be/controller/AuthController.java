package com.seapedia.be.controller;

import com.seapedia.be.dto.AuthResponse;
import com.seapedia.be.dto.ChooseRoleRequest;
import com.seapedia.be.dto.LoginRequest;
import com.seapedia.be.dto.RegisterRequest;
import com.seapedia.be.security.AuthenticatedUser;
import com.seapedia.be.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/active-role")
    public AuthResponse chooseActiveRole(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ChooseRoleRequest request
    ) {
        return authService.chooseActiveRole(user.username(), request);
    }

    @PostMapping("/logout")
    public void logout() {
        // JWT logout is handled on the client by deleting the stored token.
    }
}