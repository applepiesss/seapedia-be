package com.seapedia.be.dto;

import com.seapedia.be.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RegisterRequest(
        @NotBlank String username,
        @Email @NotBlank String email,
        @jakarta.validation.constraints.Pattern(regexp = "^(\\+?[0-9]{10,15})?$", message = "Invalid phone number format") String phoneNumber,
        @NotBlank @Size(min = 8) String password,
        @NotEmpty Set<Role> roles
) {}