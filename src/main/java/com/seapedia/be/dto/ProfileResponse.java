package com.seapedia.be.dto;

import com.seapedia.be.enums.Role;

import java.util.Set;

public record ProfileResponse(
        String username,
        String email,
        String phoneNumber,
        Set<Role> roles,
        Role activeRole,
        FinancialSummaryPlaceholder financialSummary
) {}