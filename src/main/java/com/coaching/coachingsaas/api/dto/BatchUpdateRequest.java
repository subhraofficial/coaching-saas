package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BatchUpdateRequest {

    @NotBlank
    public String name;

    @NotNull
    @Min(0)
    public Integer monthlyFee;

    @NotBlank
    public String startDate;

    @NotBlank
    public String expiryDate;
}