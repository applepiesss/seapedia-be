package com.seapedia.be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreRequest(
        @NotBlank @Size(max = 80) String storeName
) {}