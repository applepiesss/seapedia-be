package com.seapedia.be.dto;

import com.seapedia.be.enums.Role;
import jakarta.validation.constraints.NotNull;

public record ChooseRoleRequest(
        @NotNull Role activeRole
) {}