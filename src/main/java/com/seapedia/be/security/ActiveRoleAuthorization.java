package com.seapedia.be.security;

import com.seapedia.be.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("activeRoleAuthorization")
public class ActiveRoleAuthorization {
    public boolean hasActiveRole(Authentication authentication, Role requiredRole) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return false;
        }

        return requiredRole == user.activeRole();
    }
}