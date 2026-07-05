package com.seapedia.be.security;

import com.seapedia.be.enums.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtService.parseToken(token);
            String username = claims.getSubject();

            @SuppressWarnings("unchecked")
            List<String> roleNames = claims.get("roles", List.class);

            Set<Role> roles = roleNames.stream()
                    .map(Role::valueOf)
                    .collect(Collectors.toSet());

            String activeRoleName = claims.get("activeRole", String.class);
            Role activeRole = activeRoleName == null ? null : Role.valueOf(activeRoleName);

            AuthenticatedUser principal = new AuthenticatedUser(username, roles, activeRole);

            List<SimpleGrantedAuthority> authorities = activeRole == null
                    ? List.of()
                    : List.of(new SimpleGrantedAuthority("ROLE_" + activeRole.name()));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ignored) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}