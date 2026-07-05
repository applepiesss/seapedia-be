package com.seapedia.be.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @NotBlank @Size(max = 100) String reviewerName,
        @Min(1) @Max(5) Integer rating,
        @NotBlank @Size(max = 500) String comment
) {}
