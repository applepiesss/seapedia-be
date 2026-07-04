package com.seapedia.be.dto;

import com.seapedia.be.enums.Role;

import java.util.Set;

public record AuthResponse(
        String token,
        String username,
        Set<Role> roles,
        Role activeRole,
        boolean mustChooseRole
) {}