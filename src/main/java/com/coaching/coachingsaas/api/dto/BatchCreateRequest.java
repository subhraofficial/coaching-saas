package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class BatchCreateRequest {
    @NotBlank public String name;

    @Min(0) public int monthlyFee;

    @NotNull public LocalDate startDate;
    @NotNull public LocalDate expiryDate;
}