package com.seapedia.be.security;

import com.seapedia.be.enums.Role;

import java.util.Set;

public record AuthenticatedUser(
        String username,
        Set<Role> roles,
        Role activeRole
) {}